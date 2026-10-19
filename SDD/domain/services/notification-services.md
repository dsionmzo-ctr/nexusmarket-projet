# Servicios de Notificaciones

## Introducción

Aviso a las personas afectadas por un cambio de negocio mediante el puerto de salida de notificaciones.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **1**.

---

## NotifyPersonService

Envía una notificación a una persona (comprador, vendedor o usuario) a través de NotificationPort.

```java
void execute(Person recipient, String subject, String message)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `NotificationPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---
