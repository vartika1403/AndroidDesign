package com.example.practiceproject.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.practiceproject.repository.DetailsRepository
import com.example.practiceproject.ui.details.DetailsViewModel
import com.example.practiceproject.utils.Resource
import com.example.practiceproject.R
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailsViewModelTest {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    private lateinit var detailsViewModel: DetailsViewModel

    @Mock
    private lateinit var detailsRepository: DetailsRepository

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var mandiLiveData: Observer<Resource>

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
        detailsViewModel = DetailsViewModel()
    }

    @Test
    fun `test loading state from json`() {
       testCoroutineScope.runBlockingTest {
           detailsViewModel.mandiData.observeForever(mandiLiveData)
           detailsViewModel.getDataFromJson(context)
           assertEquals(detailsViewModel.mandiData.value,Resource.Loading())
       }
    }

    @Test
    fun `test data from json is success`() {
        testCoroutineScope.runBlockingTest {
            detailsViewModel.mandiData.observeForever(mandiLiveData)
            `when`(context.resources.openRawResource(R.raw.mandi)).thenReturn("")
            `when`(detailsRepository.parseJson(context)).thenReturn("")
            `when`(detailsRepository.loadDataFromJson(context)).thenReturn(
                flowOf(Resource.Error("")))

           detailsViewModel.getDataFromJson(context)
            verify(mandiLiveData).onChanged(Resource.Error(""))

        }
    }

    @After
    fun teatDown() {
        Dispatchers.resetMain()
    }
}