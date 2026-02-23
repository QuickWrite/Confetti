/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor.data;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ConfigDependencies {
    private final TypeElement baseTypeElement;

    private final List<TypeMirror> typeMirrors;
    private final List<Boolean> available;

    public ConfigDependencies(final TypeElement baseTypeElement, final List<TypeMirror> typeMirrors, final Set<TypeMirror> alreadyAvailable) {
        this.baseTypeElement = baseTypeElement;

        this.typeMirrors = typeMirrors;
        this.available = new ArrayList<>(typeMirrors.size());

        for (final TypeMirror typeMirror : this.typeMirrors) {
            this.available.add(alreadyAvailable.contains(typeMirror));
        }
    }

    public void setAvailable(final TypeMirror typeMirror) {
        final int position = this.typeMirrors.indexOf(typeMirror);

        if (position == -1) {
            return;
        }

        this.available.set(position, true);
    }

    public boolean allAvailable() {
        for (boolean available : this.available) {
            if (!available) {
                return false;
            }
        }

        return true;
    }

    public TypeElement getBaseTypeElement() {
        return this.baseTypeElement;
    }
}
