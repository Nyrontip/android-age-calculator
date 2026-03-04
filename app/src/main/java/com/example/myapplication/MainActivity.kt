package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    var nombre by remember { mutableStateOf("") }
    var anioNacimiento by remember { mutableStateOf("") }
    var anioActual by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Datos Básicos") }
            )
        },
        floatingActionButton = {
            FabCalcular(
                nombre,
                anioNacimiento,
                anioActual,
                snackbarHostState
            ) { nuevoResultado ->
                resultado = nuevoResultado
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FormularioDatos(
                nombre = nombre,
                onNombreChange = { nombre = it },
                anioNacimiento = anioNacimiento,
                onNacimientoChange = { anioNacimiento = it },
                anioActual = anioActual,
                onActualChange = { anioActual = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResultadoEdad(resultado)
        }
    }
}

@Composable
fun FormularioDatos(
    nombre: String,
    onNombreChange: (String) -> Unit,
    anioNacimiento: String,
    onNacimientoChange: (String) -> Unit,
    anioActual: String,
    onActualChange: (String) -> Unit
) {
    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = anioNacimiento,
        onValueChange = onNacimientoChange,
        label = { Text("Año de nacimiento") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )

    OutlinedTextField(
        value = anioActual,
        onValueChange = onActualChange,
        label = { Text("Año actual") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}

@Composable
fun ResultadoEdad(resultado: String) {
    Text(text = resultado)
}

@Composable
fun FabCalcular(
    nombre: String,
    anioNacimiento: String,
    anioActual: String,
    snackbarHostState: SnackbarHostState,
    onResultadoCalculado: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    FloatingActionButton(
        onClick = {

            if (nombre.isBlank() || anioNacimiento.isBlank() || anioActual.isBlank()) {
                scope.launch {
                    snackbarHostState.showSnackbar("Complete todos los campos")
                }
                return@FloatingActionButton
            }

            val nacimiento = anioNacimiento.toIntOrNull()
            val actual = anioActual.toIntOrNull()

            if (nacimiento == null || actual == null) {
                scope.launch {
                    snackbarHostState.showSnackbar("Ingrese años válidos")
                }
                return@FloatingActionButton
            }

            if (actual < nacimiento) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "El año actual no puede ser menor que el de nacimiento"
                    )
                }
                return@FloatingActionButton
            }

            val edad = actual - nacimiento

            val resultado = """
                Nombre: $nombre
                Año de nacimiento: $anioNacimiento
                Año actual: $anioActual
                Edad: $edad
            """.trimIndent()

            onResultadoCalculado(resultado)

            scope.launch {
                snackbarHostState.showSnackbar("Datos calculados correctamente")
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Calcular"
        )
    }
}