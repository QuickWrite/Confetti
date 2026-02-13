/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FilerCodeWriterTest {

    @TempDir
    Path tempDir;

    Filer filer;

    /**
     * Minimal Filer used by tests. Writes created files under the temporary directory.
     */
    static class SimpleFiler implements Filer {
        private final Path base;

        SimpleFiler(Path base) {
            this.base = Objects.requireNonNull(base, "base");
        }

        private Path sourcePathFor(String qualifiedName) {
            String rel = qualifiedName.replace('.', '/') + ".java";
            return base.resolve("sources").resolve(rel);
        }

        private Path resourcePathFor(JavaFileManager.Location loc, String pkg, String relativeName) {
            String pkgPath = (pkg == null || pkg.isEmpty()) ? "" : pkg.replace('.', '/');
            Path dir = base.resolve("resources").resolve(loc.getName().toLowerCase());
            if (!pkgPath.isEmpty()) dir = dir.resolve(pkgPath);
            return dir.resolve(relativeName);
        }

        private JavaFileObject javaFileObjectFor(Path path) throws IOException {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) Files.createFile(path);
            URI uri = path.toUri();
            return new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
                @Override
                public OutputStream openOutputStream() throws IOException {
                    return Files.newOutputStream(path);
                }

                @Override
                public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
                    return Files.newBufferedReader(path, StandardCharsets.UTF_8);
                }
            };
        }

        private FileObject fileObjectFor(Path path) throws IOException {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) Files.createFile(path);
            URI uri = path.toUri();
            return new SimpleJavaFileObject(uri, JavaFileObject.Kind.OTHER) {
                @Override
                public OutputStream openOutputStream() throws IOException {
                    return Files.newOutputStream(path);
                }

                @Override
                public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
                    return Files.newBufferedReader(path, StandardCharsets.UTF_8);
                }
            };
        }

        @Override
        public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) throws IOException {
            Path p = sourcePathFor(name.toString());
            return javaFileObjectFor(p);
        }

        @Override
        public JavaFileObject createClassFile(CharSequence name, Element... originatingElements) throws IOException {
            throw new UnsupportedOperationException("createClassFile not supported by FakeFiler");
        }

        @Override
        public FileObject createResource(JavaFileManager.Location location, CharSequence moduleAndPkg, CharSequence relativeName, Element... originatingElements) throws IOException {
            Path p = resourcePathFor(location, moduleAndPkg == null ? "" : moduleAndPkg.toString(), relativeName.toString());
            return fileObjectFor(p);
        }

        @Override
        public FileObject getResource(JavaFileManager.Location location, CharSequence moduleAndPkg, CharSequence relativeName) throws IOException {
            throw new UnsupportedOperationException("getResource not supported by FakeFiler");
        }
    }

    @BeforeEach
    void setUp() {
        this.filer = new SimpleFiler(tempDir);
    }

    @Test
    public void constructorNullFilerThrowsNPE() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            new FilerCodeWriter(null, StandardLocation.CLASS_OUTPUT);
        });

        assertTrue(ex.getMessage().contains("filer"));
    }

    @Test
    public void constructorNullResourceLocationThrowsNPE() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            new FilerCodeWriter(filer, null);
        });

        assertTrue(ex.getMessage().contains("resourceLocation"));
    }

    @Test
    public void openSourceWritesUtf8File() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer, StandardLocation.SOURCE_OUTPUT);

        JCodeModel codeModel = new JCodeModel();
        JPackage pkg = codeModel._package("com.example.test");

        String payload = "package com.example.test;\npublic class MyGenerated {}";

        try (Writer w = writer.openSource(pkg, "MyGenerated.java")) {
            w.write(payload);
            w.flush();
        }

        Path expected = tempDir.resolve("sources")
                .resolve("com").resolve("example").resolve("test").resolve("MyGenerated.java");

        assertTrue(Files.exists(expected), "Generated source file must exist: " + expected);

        String content = Files.readString(expected, StandardCharsets.UTF_8);

        assertTrue(content.contains("public class MyGenerated"), "File should contain class declaration");
    }

    @Test
    public void openSourceWithNullWritesToRootSources() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer, StandardLocation.SOURCE_OUTPUT);

        // pass null as pkg to exercise pkg==null branch
        JPackage pkg = null;

        String payload = "public class TopLevel {}";

        try (Writer w = writer.openSource(pkg, "TopLevel.java")) {
            w.write(payload);
            w.flush();
        }

        Path expected = tempDir.resolve("sources").resolve("TopLevel.java");
        assertTrue(Files.exists(expected), "Generated source (unnamed package) must exist: " + expected);

        String content = Files.readString(expected, StandardCharsets.UTF_8);
        assertTrue(content.contains("public class TopLevel"), "File should contain class declaration");

        writer.close();
    }

    @Test
    public void openBinaryWritesBytesToClassOutput() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer);

        JCodeModel codeModel = new JCodeModel();
        JPackage pkg = codeModel._package("org.example.res");

        byte[] bytes = new byte[] { 10, 20, 30, 40 };

        try (OutputStream os = writer.openBinary(pkg, "data.bin")) {
            os.write(bytes);
            os.flush();
        }

        Path expected = tempDir.resolve("resources")
                .resolve("class_output")
                .resolve("org").resolve("example").resolve("res")
                .resolve("data.bin");

        assertTrue(Files.exists(expected), "Binary resource must exist: " + expected);
        byte[] read = Files.readAllBytes(expected);
        assertArrayEquals(bytes, read, "Written bytes must match read bytes");
    }

    @Test
    public void openSourceNullFileNameThrowsNPE() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer, StandardLocation.SOURCE_OUTPUT);

        JCodeModel codeModel = new JCodeModel();
        JPackage pkg = codeModel._package("com.example");

        assertThrows(NullPointerException.class, () -> {
            writer.openSource(pkg, null);
        });
    }

    @Test
    public void openBinaryNullFileNameThrowsNPE() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer, StandardLocation.CLASS_OUTPUT);

        JCodeModel codeModel = new JCodeModel();
        JPackage pkg = codeModel._package("org.example");

        assertThrows(NullPointerException.class, () -> {
            writer.openBinary(pkg, null);
        });
    }

    @Test
    public void closeDoesNothing() throws Exception {
        FilerCodeWriter writer = new FilerCodeWriter(filer);

        assertDoesNotThrow(writer::close);

        try(Stream<Path> files = Files.list(tempDir)) {
            assertEquals(List.of(), files.toList());
        }
    }
}

