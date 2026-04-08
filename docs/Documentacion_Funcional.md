# Documentación Funcional - API Gestión de Fondos BTG Pactual

## 1. Objetivo del Producto
La plataforma permite a los clientes de BTG Pactual gestionar de forma autónoma sus fondos de inversión mediante una API REST. Esta solución elimina la necesidad de contactar a un asesor humano para procesos básicos como adquirir nuevos fondos, recuperar la inversión o visualizar la actividad histórica de su cuenta.

## 2. Tipos de Usuarios y Perfiles
El sistema está diseñado para un actor central:
* **Cliente Inversor:** Persona con una cuenta activa y un saldo (por defecto configurado inicialmente con COP $500.000). El cliente debe estar autenticado en el sistema para realizar cualquier tipo de operación.

## 3. Catálogo de Fondos Soportados
El sistema ofrece inicialmente 5 fondos de inversión, divididos por categorías y cada uno con un monto mínimo exigido de vinculación.
1. **FPV_BTG_PACTUAL_RECAUDADORA** (Cat: FPV) - Mínimo: $75.000 COP
2. **FPV_BTG_PACTUAL_ECOPETROL** (Cat: FPV) - Mínimo: $125.000 COP
3. **DEUDAPRIVADA** (Cat: FIC) - Mínimo: $50.000 COP
4. **FDO-ACCIONES** (Cat: FIC) - Mínimo: $250.000 COP
5. **FPV_BTG_PACTUAL_DINAMICA** (Cat: FPV) - Mínimo: $100.000 COP

## 4. Flujos Principales de Negocio

### 4.1. Apertura / Suscripción de un Fondo
El usuario selecciona un fondo y especifica el monto a invertir.
**Reglas de negocio validadas:**
- El monto a invertir no puede ser inferior al *Monto mínimo* exigido por el fondo.
- El usuario debe contar con *Saldo disponible* en su cuenta superior o igual al monto especificado. Si no lo tiene, el sistema rechaza la operación informando: *"No tiene saldo disponible para vincularse al fondo <Nombre del fondo>"*.
- Tras el éxito de la suscripción, el saldo se descuenta automáticamente de la cuenta del usuario.
- El sistema dispara una notificación asíncrona informando del éxito (vía SMS o Correo Electrónico, según las preferencias guardadas del cliente).

### 4.2. Cancelación de un Fondo
El usuario puede decidir retirar su dinero de un fondo vigente.
**Reglas de negocio validadas:**
- El usuario debe tener una suscripción activa previa en dicho fondo.
- El sistema consolidará todo el flujo para este fondo (revisando cuánto depositó inicialmente) y regresará íntegramente el valor de vinculación a su saldo general, dejando el monto disponible para otras futuras inversiones.

### 4.3. Historial de Transacciones
El usuario puede consultar una auditoría detallada de sus finanzas, visualizando cada deposito y cada retiro (suscripciones y cancelaciones) que ha realizado históricamente, de forma cronológica.
