package com.aistudio.dieselstationsms.kxmpzq

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppDrawer
import com.aistudio.dieselstationsms.kxmpzq.ui.components.BottomNavBar
import com.aistudio.dieselstationsms.kxmpzq.ui.navigation.AppNavigation
import com.aistudio.dieselstationsms.kxmpzq.ui.navigation.Screen
import com.aistudio.dieselstationsms.kxmpzq.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ═══════════════════════════════════════════════════════════════
 * MainActivity — النسخة Native باستخدام Jetpack Compose
 * ═══════════════════════════════════════════════════════════════
 *
 * تم إزالة:
 * - WebView + web_interface.html
 * - JavaScriptInterface
 * - loadWebViewFromAssets()
 * - serverReady flags
 *
 * تم إضافته:
 * - Navigation Compose
 * - ModalNavigationDrawer
 * - BottomNavigationBar
 * - Direct Repository Access
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start SMS Service (بدون NanoHTTPD)
        lifecycleScope.launch {
            delay(2000)
            startSMSService()
        }

        requestAllPermissions()

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                StationApp()
            }
        }
    }

    @Composable
    private fun StationApp() {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Dashboard.route

        // Hide drawer & bottom nav on login screen
        val isLoginScreen = currentRoute == Screen.Login.route

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isLoginScreen,
            drawerContent = {
                if (!isLoginScreen) {
                    AppDrawer(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onClose = { scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!isLoginScreen) {
                        BottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { screen ->
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                AppNavigation(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    onLogout = {
                        // Clear any session data if needed
                    }
                )
            }
        }
    }

    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_SMS)
        }
        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun startSMSService() {
        try {
            val intent = android.content.Intent(this, SMSService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Log.d(TAG, "SMSService started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start SMSService: ${e.message}", e)
        }
    }
}
