# 🚀 Notifications Library (Java 21)

Una librería de notificaciones robusta, extensible, altamente testeada (cobertura >90%) y completamente **agnóstica a frameworks** (sin dependencias de Spring, Quarkus, etc.). Diseñada bajo principios de arquitectura limpia y patrones de diseño SOLID para unificar el envío de alertas mediante múltiples canales.

---

## 🛠️ Características Principales

- **Agnóstica a Frameworks:** Configuración 100% mediante código Java puro (sin archivos YAML, properties ni anotaciones de inyección de dependencias).
- **Múltiples Canales Obligatorios:** Implementación para **Email** (SendGrid), **SMS** (Twilio) y **Push Notifications** (Firebase).
- **Resiliencia & Tolerancia a Fallos:** Sistema de reintentos automático configurable ante fallas temporales de red o API.
- **Soporte Asíncrono:** Envíos no bloqueantes utilizando `CompletableFuture`.
- **Validaciones Estrictas:** Validación previa de formatos (emails válidos, teléfonos, etc.) antes de interactuar con proveedores.
- **Alta Cobertura de Pruebas:** Cobertura de código superior al 90% con pruebas unitarias que simulan escenarios reales y de error.

---

## 🏗️ Patrones de Diseño & SOLID Aplicados

- **Strategy Pattern:** La interfaz común `NotificationProvider` permite que cada canal/proveedor defina de manera independiente cómo procesar la notificación.
- **Fluent Builder:** Facilita la creación y parametrización de los objetos `Notification` y del cliente `NotificationService`.
- **Dependency Inversion (SOLID):** Quien use la librería depende únicamente de la abstracción `NotificationService`, logrando un desacoplamiento absoluto de los proveedores reales.
- **Open/Closed Principle (SOLID):** Es sumamente sencillo agregar un nuevo proveedor o canal (ej. Slack, Discord) simplemente creando una clase que implemente `NotificationProvider`, sin necesidad de modificar el código existente en el núcleo de la librería.

---

## 📦 Instalación

Para importar y usar esta librería en un proyecto Maven externo, primero debes compilarla e instalarla en tu repositorio local `.m2`:

```bash
# Ejecutar en la raíz de la librería
mvn clean install