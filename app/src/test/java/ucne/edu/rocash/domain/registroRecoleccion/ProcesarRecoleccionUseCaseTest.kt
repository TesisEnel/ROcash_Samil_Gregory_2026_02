package ucne.edu.rocash.domain.registroRecoleccion

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.domain.agenteVentas.usecase.SumarDeudaAgenteUseCase
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import ucne.edu.rocash.domain.registroRecoleccion.usecase.CalcularCuadreUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase

/**
 * Es el caso de uso con más consecuencias de la app: escribe el registro, carga
 * la deuda al agente, marca la estación como completada y mueve la ruta a
 * EN_PROGRESO. Estaba sin un solo test.
 *
 * `CalcularCuadreUseCase` se instancia real y no se mockea: es un cálculo puro,
 * y así el test comprueba que la deuda cargada al agente es la que la fórmula
 * produce, en lugar de un número escrito a mano que podría desviarse.
 */
class ProcesarRecoleccionUseCaseTest {

    private lateinit var repository: RegistroRecoleccionRepository
    private lateinit var hojaRutaRepository: HojaRutaRepository
    private lateinit var sumarDeudaAgenteUseCase: SumarDeudaAgenteUseCase
    private lateinit var useCase: ProcesarRecoleccionUseCase

    private val rutaPendiente = HojaRuta(
        id = 7,
        recolectorId = "uid-cobrador",
        estado = EstadoRuta.PENDIENTE
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        hojaRutaRepository = mockk(relaxed = true)
        sumarDeudaAgenteUseCase = mockk(relaxed = true)

        useCase = ProcesarRecoleccionUseCase(
            repository = repository,
            hojaRutaRepository = hojaRutaRepository,
            sumarDeudaAgenteUseCase = sumarDeudaAgenteUseCase,
            calcularCuadreUseCase = CalcularCuadreUseCase()
        )

        coEvery { hojaRutaRepository.obtenerRuta(7) } returns rutaPendiente
        coEvery { repository.obtenerPorRutaYEstacion(7, 3) } returns null
        coEvery { repository.upsert(any()) } returns 55
    }

    private suspend fun procesar(
        ventaBruta: String = "10000",
        comision: String = "2000",
        recolectado: String = "8000",
        agenteId2: Int? = null
    ) = useCase(
        hojaRutaId = 7,
        estacionId = 3,
        agenteId1 = 1,
        agenteId2 = agenteId2,
        ventaBrutaStr = ventaBruta,
        comisionClienteStr = comision,
        montoRecolectadoStr = recolectado
    )

    @Test
    fun `un cuadre completo guarda el registro y marca la estacion`() = runTest {
        val resultado = procesar()

        assertTrue(resultado.isSuccess)
        assertEquals(55, resultado.getOrThrow())

        coVerify(exactly = 1) { repository.upsert(any<RegistroRecoleccion>()) }
        coVerify(exactly = 1) {
            hojaRutaRepository.marcarEstadoEstacion(7, 3, EstadoVisitaEstacion.COMPLETADA)
        }
    }

    @Test
    fun `cuadrar la primera banca mueve la ruta a EN_PROGRESO`() = runTest {
        procesar()

        coVerify(exactly = 1) {
            hojaRutaRepository.cambiarEstadoRuta(7, EstadoRuta.EN_PROGRESO)
        }
    }

    @Test
    fun `una ruta ya en progreso no se vuelve a mover de estado`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaPendiente.copy(estado = EstadoRuta.EN_PROGRESO)

        procesar()

        coVerify(exactly = 0) { hojaRutaRepository.cambiarEstadoRuta(any(), any()) }
    }

    @Test
    fun `sin faltante no se le carga deuda a nadie`() = runTest {
        procesar(recolectado = "8000")

        coVerify(exactly = 0) { sumarDeudaAgenteUseCase(any(), any()) }
    }

    @Test
    fun `el faltante se le carga completo al agente unico`() = runTest {
        procesar(recolectado = "5000")

        coVerify(exactly = 1) { sumarDeudaAgenteUseCase(1, 3_000.0) }
    }

    @Test
    fun `en una banca de dos agentes el faltante se reparte por mitades`() = runTest {
        procesar(recolectado = "5000", agenteId2 = 4)

        coVerify(exactly = 1) { sumarDeudaAgenteUseCase(1, 1_500.0) }
        coVerify(exactly = 1) { sumarDeudaAgenteUseCase(4, 1_500.0) }
    }

    @Test
    fun `corregir un cuadre solo mueve la diferencia de deuda`() = runTest {
        // Ya habia un cuadre con 3000 de deuda. El nuevo deja 1000, asi que al
        // agente hay que devolverle 2000, no cargarle otros 1000.
        coEvery { repository.obtenerPorRutaYEstacion(7, 3) } returns RegistroRecoleccion(
            recoleccionId = 42,
            hojaRutaId = 7,
            estacionId = 3,
            ventaBruta = 10_000.0,
            comisionCliente = 2_000.0,
            montoEsperado = 8_000.0,
            montoRecolectado = 5_000.0,
            montoDeuda = 3_000.0
        )

        procesar(recolectado = "7000")

        coVerify(exactly = 1) { sumarDeudaAgenteUseCase(1, -2_000.0) }
    }

    @Test
    fun `corregir un cuadre reutiliza el id del registro anterior`() = runTest {
        coEvery { repository.obtenerPorRutaYEstacion(7, 3) } returns RegistroRecoleccion(
            recoleccionId = 42,
            hojaRutaId = 7,
            estacionId = 3,
            ventaBruta = 10_000.0,
            comisionCliente = 2_000.0,
            montoEsperado = 8_000.0,
            montoRecolectado = 8_000.0,
            montoDeuda = 0.0
        )

        procesar()

        // Si el id no se reutilizara, cada correccion crearia un registro nuevo
        // y la banca acabaria con varios cuadres en la misma ruta.
        coVerify(exactly = 1) {
            repository.upsert(match<RegistroRecoleccion> { it.recoleccionId == 42 })
        }
    }

    @Test
    fun `una ruta cerrada no admite mas cuadres`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaPendiente.copy(estado = EstadoRuta.CERRADA)

        val resultado = procesar()

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { repository.upsert(any()) }
        coVerify(exactly = 0) { sumarDeudaAgenteUseCase(any(), any()) }
    }

    @Test
    fun `una ruta inexistente falla sin tocar nada`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns null

        assertTrue(procesar().isFailure)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `montos no numericos se rechazan antes de tocar la base`() = runTest {
        assertTrue(procesar(ventaBruta = "abc").isFailure)
        assertTrue(procesar(comision = "").isFailure)
        assertTrue(procesar(recolectado = "-500").isFailure)

        coVerify(exactly = 0) { repository.upsert(any()) }
        coVerify(exactly = 0) { sumarDeudaAgenteUseCase(any(), any()) }
    }

    @Test
    fun `una comision mayor que la venta se rechaza`() = runTest {
        val resultado = procesar(ventaBruta = "1000", comision = "3000", recolectado = "0")

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `recolectar mas de lo esperado se rechaza`() = runTest {
        val resultado = procesar(recolectado = "9000")

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }
}
