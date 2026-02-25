/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public interface SimpleProcessor {
    void process(final TypeElement annotation, final RoundEnvironment roundEnv);

    void roundEnding(final RoundEnvironment roundEnv);

    Class<?> annotation();
}
