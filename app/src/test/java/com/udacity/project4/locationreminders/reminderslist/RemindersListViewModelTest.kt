package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var appContext: Application
    private lateinit var repository: ReminderDataSource

    // Inject the fake data source
    private val fdataSource: FakeDataSource by KoinJavaComponent.inject(FakeDataSource::class.java)
    private val emptyDataSource: FakeDataSource by KoinJavaComponent.inject(FakeDataSource::class.java)

    // Inject the ViewModel
    private val viewModel: RemindersListViewModel by KoinJavaComponent.inject(RemindersListViewModel::class.java)

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()

        val myModule = module {
            viewModel { RemindersListViewModel(appContext, fdataSource) }
            single<ReminderDataSource> { FakeDataSource(get()) }
        }

        // Declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }

        //Get our real repository
        repository = koinApplication().koin.get()

    }

    // Provide testing to the RemindersListViewModel and its live data objects
    @Test
    suspend fun viewModel_getReminder() {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val value = fdataSource.getReminder(reminder.id)
        assert(value != null)

    }

    // Provide testing to the RemindersListViewModel // NEEDS CONTINUATION HERE - SEE udacity
    @Test
    fun viewModel_loadReminders() {
        viewModel.loadReminders()
    }

    // Provide testing to the RemindersListViewModel and its live data objects
    @Test
    fun viewModel_remindersList_LiveData() {
        viewModel.loadReminders()

        val value = viewModel.remindersList.value
        if (value != null) {
            assert(value.isNotEmpty())
        } else {
            assert(false)
        }
    }
}
