ROcash 💸

ROcash es una aplicación móvil desarrollada nativamente para Android, diseñada para optimizar la logística operativa y la gestión financiera de los recolectores de efectivo en campo. La aplicación permite organizar rutas de recolección de manera eficiente y llevar un control riguroso de los flujos de caja, comisiones y deudas acumuladas por los agentes de ventas.

🎯 Propósito del Proyecto

El objetivo principal de ROcash es eliminar la fricción operativa en el proceso de recolección diaria de dinero. La aplicación permite:

Logística de Rutas: Crear hojas de ruta dinámicas asignando estaciones (bancas) específicas, permitiendo un seguimiento del avance en tiempo real.

Cuadre de Efectivo: Registro preciso de venta bruta, comisiones y recolección efectiva en cada estación, automatizando el cálculo de faltantes o sobrantes.

Gestión de Cuentas por Cobrar: Identificación automática de deudas generadas por el agente de ventas cuando el efectivo recolectado no cubre la venta neta.

Historial de Abonos: Un sistema de tracking para el saldado de deudas, permitiendo registrar abonos parciales o liquidaciones totales, manteniendo una trazabilidad financiera completa.

🚀 Características Clave

Auditoría Histórica: Cada ruta cerrada se convierte en un documento histórico inmutable, garantizando la integridad de los datos financieros de días anteriores.

Desglose Financiero: Un Dashboard inteligente que suma los ingresos obtenidos en ruta y los abonos realizados fuera de ruta, ofreciendo una visión real del flujo de caja.

Gestión de Agentes y Estaciones: Base de datos relacional para gestionar la asignación de agentes a bancas específicas, facilitando la auditoría de cada estación.

Arquitectura Robusta: Desarrollado bajo los principios de Clean Architecture y MVI (Model-View-Intent), asegurando que la interfaz de usuario sea reactiva y el código altamente mantenible.

🛠 Stack Tecnológico

Lenguaje: Kotlin

UI: Jetpack Compose (Diseño moderno, declarativo y eficiente)

Arquitectura: Clean Architecture + MVI

Base de Datos: Room (Persistencia local relacional)

Inyección de Dependencias: Dagger Hilt

Navegación: Jetpack Navigation (Type-safe)

Networking/Auth: Firebase Authentication

Imágenes: Coil (Carga optimizada de perfiles desde Firebase/Google)

🏗 Arquitectura de Datos

El sistema maneja la contabilidad mediante un flujo de eventos donde las deudas, generadas en el cuadre de ruta, son tratadas como saldos pendientes que el sistema gestiona mediante un DAO centralizado, permitiendo aplicar abonos que reducen la deuda acumulada del agente de manera sincronizada y consistente.

Youtube Link: https://youtu.be/ELfKCJ-W41A?si=_zVt9XUUayyrlJj4
