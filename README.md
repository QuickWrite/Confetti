<div align="center" style="text-align: center; width: 100%">
<img src="assets/logo/confetti-logo.svg" height="90px"/>
<h1>Confetti</h1>

Confetti is a lightweight, annotation‑driven Java configuration library.  
It generates type‑safe configuration classes at compile time using an annotation processor.
</div>

> [!IMPORTANT]
> This library is still in its early phases and does not have all features as most of them are still planned.

## Structure
The library is separated into four diffferent parts:
- The [core](confetti-core) that provides all generic classes and interfaces for the configurations
- The format implementers (like HOCON) which are implementing the interfaces from the core
- The [annotations](confetti-annotations) that add semantics to the code
- The [annotation processor](confetti-processor) that links the semantics of the annotations to the implementation of the core

## License
This library is licensed under the [Mozilla Public License 2.0](LICENSE.txt). So use it in your project. :D
