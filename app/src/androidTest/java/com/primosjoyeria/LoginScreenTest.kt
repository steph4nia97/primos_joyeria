import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.primosjoyeria.ui.theme.AuthViewModel
import com.primosjoyeria.ui.theme.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * 1) Verifica que se muestra el logo y el texto de bienvenida.
     */
    @Test
    fun login_muestraLogoYTextoBienvenida() {
        val authVm = AuthViewModel() // ViewModel real

        composeRule.setContent {
            LoginScreen(
                authViewModel = authVm,
                alIniciarSesion = {},
                alRegistrarClick = {},
                onAdminClick = {}
            )
        }

        // Texto de bienvenida
        composeRule
            .onNodeWithText("Bienvenido a Primos Joyería")
            .assertIsDisplayed()

        // Logo: contentDescription = "Logo Primos Joyería"
        composeRule
            .onNodeWithContentDescription("Logo Primos Joyería")
            .assertIsDisplayed()
    }

    /**
     * 2) El botón "Iniciar sesión" está deshabilitado cuando los campos están vacíos.
     */
    @Test
    fun login_botonDeshabilitadoConCamposVacios() {
        val authVm = AuthViewModel()

        composeRule.setContent {
            LoginScreen(
                authViewModel = authVm,
                alIniciarSesion = {},
                alRegistrarClick = {},
                onAdminClick = {}
            )
        }

        composeRule
            .onNodeWithText("Iniciar sesión")
            .assertIsNotEnabled()
    }

    /**
     * 3) Al escribir correo y contraseña el botón "Iniciar sesión" se habilita.
     */
    @Test
    fun login_botonSeHabilitaAlIngresarEmailYPassword() {
        val authVm = AuthViewModel()

        composeRule.setContent {
            LoginScreen(
                authViewModel = authVm,
                alIniciarSesion = {},
                alRegistrarClick = {},
                onAdminClick = {}
            )
        }

        // Escribir correo en el TextField (usa la label)
        composeRule
            .onNodeWithText("Correo electrónico")
            .performTextInput("test@correo.cl")

        // Escribir contraseña
        composeRule
            .onNodeWithText("Contraseña")
            .performTextInput("1234")

        // Ahora el botón debería estar habilitado
        composeRule
            .onNodeWithText("Iniciar sesión")
            .assertIsEnabled()
    }
}