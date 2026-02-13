/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link CodeWriter} implementation that routes {@code .java} source files and binary
 * resources through an annotation-processor {@link Filer}.
 *
 * <p>
 * This is useful when building a {@code JCodeModel} model in an annotation processor
 * whilst the generated sources/resources should be created using the compiler's {@link Filer}
 * (so the files are visible to the compiler and handled by incremental build tools).
 *
 * <p>
 * <h2>Behavior summary:</h2>
 * <ul>
 *   <li>
 *       {@link #openSource(JPackage, String)} calls {@link Filer#createSourceFile(CharSequence, Element...)}
 *       and returns a UTF-8 {@link Writer} that writes into the returned {@link JavaFileObject}.
 *   </li>
 *   <li>
 *       {@link #openBinary(JPackage, String)} calls
 *       {@link Filer#createResource(JavaFileManager.Location, CharSequence, CharSequence, Element...)}
 *       and returns the {@link OutputStream} of the created {@link FileObject}.
 *   </li>
 *   <li>
 *       By default binary resources are created in {@link StandardLocation#CLASS_OUTPUT} but you can
 *       specify another location (for example {@link StandardLocation#SOURCE_OUTPUT}).
 *   </li>
 * </ul>
 *
 * <p>
 * Note: the {@link Filer} will throw a {@code FilerException} if the same file/resource is
 * created more than once within the same compilation round - callers should avoid duplicate creation.
 */
public class FilerCodeWriter extends CodeWriter {
    private final Filer filer;
    private final StandardLocation resourceLocation;

    /**
     * Construct a writer that uses {@code CLASS_OUTPUT} for binary resources.
     *
     * @param filer the annotation-processor filer used to create files
     */
    public FilerCodeWriter(final Filer filer) {
        this(filer, StandardLocation.CLASS_OUTPUT);
    }

    /**
     * Construct a writer and choose where binary resources are created.
     *
     * @param filer the annotation-processor filer used to create files
     * @param resourceLocation where binary resources should be created (e.g. {@link StandardLocation#CLASS_OUTPUT}
     *                         or {@link StandardLocation#SOURCE_OUTPUT})
     */
    public FilerCodeWriter(final Filer filer, final StandardLocation resourceLocation) {
        Objects.requireNonNull(filer, "filer must not be null");
        Objects.requireNonNull(resourceLocation, "resourceLocation must not be null");

        this.filer = filer;
        this.resourceLocation = resourceLocation;
    }

    /**
     * Open a writer for a Java source file. JCodeModel calls this for each generated source file.
     *
     * <p>
     * The package and fileName are combined to produce the fully qualified class name used
     * with {@link Filer#createSourceFile(CharSequence, javax.lang.model.element.Element...)}.
     * The returned {@link Writer} writes UTF-8 bytes to the underlying {@link JavaFileObject}.
     *
     * @param pkg the JCodeModel package (may be unnamed)
     * @param fileName the file name including the ".java" suffix (JCodeModel passes the file name)
     * @return a UTF-8 {@link Writer} to write source text
     * @throws IOException if creating the file fails
     */
    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        final String qualified = (pkg == null || pkg.isUnnamed() ? "" : pkg.name() + ".")
                                  + fileName.replaceAll("\\.java$", "");
        final JavaFileObject jfo = filer.createSourceFile(qualified);
        return new OutputStreamWriter(jfo.openOutputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Open a binary resource for writing.
     *
     * <p>
     * This uses {@link Filer#createResource(JavaFileManager.Location, CharSequence, CharSequence, Element...)}
     * with the {@link #resourceLocation} provided in the constructor.
     *
     * @param pkg the JCodeModel package or {@code null} for unnamed
     * @param fileName the resource relative name (for example {@code "META-INF/services/..."} or {@code "foo.properties"})
     * @return an {@link OutputStream} for writing bytes
     * @throws IOException if resource creation fails
     */
    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        final String pkgName = (pkg == null || pkg.isUnnamed()) ? "" : pkg.name();

        final FileObject fo = filer.createResource(resourceLocation, pkgName, fileName);

        return fo.openOutputStream();
    }

    /**
     * Close the writer. There is nothing to close globally as each individual stream/writer is
     * closed by the caller after writing. This is provided for API completeness.
     */
    @Override
    public void close() throws IOException {
        // nothing global to close
    }
}
