package com.example.fitquest.ui.nutrition

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NutritionViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val applicationMock = mock(Application::class.java)
    private val viewModel = NutritionViewModel(applicationMock)

    @Test
    fun addMultipleEntries_updatesListCorrectly() {
        val entriesToAdd = listOf(
            RowEntry("Apple", 95.0),
            RowEntry("Banana", 105.0),
            RowEntry("Orange", 45.0)
        )

        entriesToAdd.forEach { viewModel.addEntry(it) }

        val latch = CountDownLatch(1)
        val observer = Observer<List<RowEntry>> { if (it.size == entriesToAdd.size) latch.countDown() }
        viewModel.entriesLiveData.observeForever(observer)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData did not emit in time")
        }

        val entries = viewModel.entriesLiveData.value ?: emptyList()
        assertEquals("List size should match the number of added entries",
            entriesToAdd.size, entries.size)

        viewModel.entriesLiveData.removeObserver(observer)
    }
    @Test
    fun addEntry_updatesListAndTotalCalories() {
        val entry = RowEntry("Apple", 95.0)
        val latch = CountDownLatch(1)

        val observer = Observer<List<RowEntry>> {
            if (it.contains(entry)) {
                latch.countDown()
            }
        }

        viewModel.entriesLiveData.observeForever(observer)
        viewModel.addEntry(entry)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData did not emit in time")
        }

        val entries = viewModel.entriesLiveData.value ?: emptyList()
        assertTrue("List should contain the added entry", entries.contains(entry))

        viewModel.entriesLiveData.removeObserver(observer)
    }
    @Test
    fun updateTotalCalories_calculatesCorrectTotal() {
        viewModel.addEntry(RowEntry("Apple", 95.0))
        viewModel.addEntry(RowEntry("Banana", 105.0))

        val latch = CountDownLatch(1)
        val observer = Observer<Double> {
            latch.countDown()
        }

        viewModel.totalCaloriesLiveData.observeForever(observer)
        viewModel.updateTotalCalories()

        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData did not emit in time")
        }

        val totalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Total calories should be correctly calculated",
            200.0, totalCalories, 0.01)

        viewModel.totalCaloriesLiveData.removeObserver(observer)
    }

    @Test
    fun removeEntry_updatesListAndTotalCalories() {
        val entry = RowEntry("Apple", 95.0)
        viewModel.addEntry(entry)

        val latch = CountDownLatch(1)
        val observer = Observer<List<RowEntry>> {
            if (it.isEmpty()) {
                latch.countDown()
            }
        }

        viewModel.entriesLiveData.observeForever(observer)
        viewModel.removeEntry(0)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData did not emit in time")
        }

        val entries = viewModel.entriesLiveData.value ?: emptyList()
        assertFalse("List should not contain the removed entry", entries.contains(entry))

        viewModel.entriesLiveData.removeObserver(observer)
    }


    @Test
    fun removeNonExistentEntry_doesNotChangeList() {
        viewModel.addEntry(RowEntry("Apple", 95.0))

        val latch = CountDownLatch(1)
        val observer = Observer<List<RowEntry>> { if (it.isNotEmpty()) latch.countDown() }
        viewModel.entriesLiveData.observeForever(observer)

        viewModel.removeEntry(10)

        if (!latch.await(1, TimeUnit.SECONDS)) {
            fail("LiveData should not emit for invalid removal")
        }

        val entries = viewModel.entriesLiveData.value ?: emptyList()
        assertEquals("List size should remain unchanged", 1, entries.size)

        viewModel.entriesLiveData.removeObserver(observer)
    }

    @Test
    fun removeEntry_updatesLiveData() {
        viewModel.addEntry(RowEntry("Apple", 95.0))
        viewModel.addEntry(RowEntry("Banana", 105.0))

        val latch = CountDownLatch(1)
        val observer = Observer<List<RowEntry>> { if (it.size == 1) latch.countDown() }
        viewModel.entriesLiveData.observeForever(observer)

        viewModel.removeEntry(0)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData did not emit in time")
        }

        val entries = viewModel.entriesLiveData.value ?: emptyList()
        assertEquals("List should have one entry after removal", 1, entries.size)
        assertEquals("Remaining entry should be 'Banana'", "Banana", entries[0].textString)

        viewModel.entriesLiveData.removeObserver(observer)
    }

    @Test
    fun totalCalories_calculatedCorrectly() {
        viewModel.addEntry(RowEntry("Apple", 95.0))
        viewModel.addEntry(RowEntry("Banana", 105.0))
        viewModel.addEntry(RowEntry("Orange", 50.0))

        viewModel.updateTotalCalories()
        var totalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Total calories calculation after addition",
            250.0, totalCalories, 0.01)

        viewModel.removeEntry(1)
        viewModel.updateTotalCalories()
        totalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Total calories calculation after removal",
            145.0, totalCalories, 0.01)
    }

    @Test
    fun removeEntryWithInvalidIndex_doesNotUpdateLiveData() {
        viewModel.addEntry(RowEntry("Apple", 95.0))

        val initialEntries = viewModel.entriesLiveData.value.orEmpty()
        viewModel.removeEntry(-1)

        val updatedEntries = viewModel.entriesLiveData.value.orEmpty()
        assertEquals("Entries list should not change on invalid removal",
            initialEntries, updatedEntries)
    }

    @Test
    fun totalCaloriesCalculation_isConsistent() {
        viewModel.addEntry(RowEntry("Apple", 95.0))
        viewModel.addEntry(RowEntry("Banana", 105.0))
        viewModel.updateTotalCalories()

        var totalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Initial calculation of total calories",
            200.0, totalCalories, 0.01)

        viewModel.removeEntry(0)
        viewModel.updateTotalCalories()

        totalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Total calories after removal",
            105.0, totalCalories, 0.01)
    }
    @Test
    fun viewModel_initialState_isCorrect() {
        val initialEntries = viewModel.entriesLiveData.value.orEmpty()
        assertTrue("Initial state of entries LiveData should be empty", initialEntries.isEmpty())

        val initialTotalCalories = viewModel.totalCaloriesLiveData.value ?: 0.0
        assertEquals("Initial state of total calories should be 0.0",
            0.0, initialTotalCalories, 0.01)
    }

}