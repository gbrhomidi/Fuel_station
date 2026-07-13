package com.aistudio.dieselstationsms.kxmpzq

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepositoryImpl
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.DashboardViewModel
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.LoginViewModel
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.SalesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * ═══════════════════════════════════════════════════════════════
 * ViewModel Tests — التحقق من عمل ViewModels بدون WebView/NanoHTTPD
 * ═══════════════════════════════════════════════════════════════
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: StationRepositoryImpl
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        repository = StationRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `DashboardViewModel should load dashboard data`() = runTest {
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.dashboardState.first()
        assertTrue(state is com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState.Success)
        val data = (state as com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState.Success).data
        assertTrue(data.totalTransactions >= 0)
        assertTrue(data.totalCustomers >= 0)
    }

    @Test
    fun `LoginViewModel should fail with wrong credentials`() = runTest {
        val viewModel = LoginViewModel(repository)
        viewModel.onUsernameChange("wrong_user")
        viewModel.onPasswordChange("wrong_pass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.authState.first()
        assertTrue(state is com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState.Error)
    }

    @Test
    fun `SalesViewModel should load sales without error`() = runTest {
        val viewModel = SalesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.salesState.first()
        assertTrue(state is com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState.Success ||
                   state is com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState.Loading)
    }

    @Test
    fun `SalesViewModel dropdowns should load`() = runTest {
        val viewModel = SalesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.customers.value)
        assertNotNull(viewModel.fuelTypes.value)
        assertNotNull(viewModel.pumps.value)
    }
}
