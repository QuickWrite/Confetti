/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor.data;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the currently unknown dependencies that occur when processing
 * classes with methods of unknown types.
 *
 * <p>
 * This always occurs if there is an interface that is currently being processed, but the
 * types that have to be able to be resolved currently do not have a resolver:
 * {@snippet :
 * import net.quickwrite.confetti.ConfettiConfig;
 *
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     String test();
 * }
 * }
 * In this case the method {@code test} returns a {@link String}, but it can be that
 * there is no type resolver for the {@link String}. As the type resolver can be found
 * later on, this has to be stored and checked later on.
 */
public class UnknownDependencyList {
    private final Set<TypeMirror> availableTypes;
    private final Set<ConfigDependencies> configDependenciesSet;

    public UnknownDependencyList() {
        this(new HashSet<>());
    }

    public UnknownDependencyList(final Set<TypeMirror> availableTypes) {
        this.availableTypes = availableTypes;
        this.configDependenciesSet = new HashSet<>();
    }

    public boolean setAvailable(final TypeMirror typeMirror) {
        if (!this.availableTypes.add(typeMirror)) {
            return false;
        }

        for (final ConfigDependencies configDependencies : this.configDependenciesSet) {
            configDependencies.setAvailable(typeMirror);
        }

        return true;
    }

    public Collection<TypeElement> collectKnown() {
        final Collection<ConfigDependencies> known = configDependenciesSet.stream()
                .filter(ConfigDependencies::allAvailable).toList();

        known.forEach(configDependenciesSet::remove);

        return known.stream().map(ConfigDependencies::getBaseTypeElement).toList();
    }

    public Collection<TypeElement> collectCurrentConfigs() {
        return this.configDependenciesSet.stream().map(ConfigDependencies::getBaseTypeElement).toList();
    }

    public boolean addConfigWithDependencies(final TypeElement baseTypeElement, final List<TypeMirror> typeMirrors) {
        final ConfigDependencies dependencies = new ConfigDependencies(baseTypeElement, typeMirrors, availableTypes);

        if (!this.configDependenciesSet.add(dependencies)) {
            return false;
        }

        return this.setAvailable(baseTypeElement.asType());
    }

    public Set<TypeMirror> missingTypes() {
        final Set<TypeMirror> missingTypes = new HashSet<>();

        for (final ConfigDependencies configDependencies : this.configDependenciesSet) {
            missingTypes.addAll(configDependencies.missingTypes());
        }

        return missingTypes;
    }
}
