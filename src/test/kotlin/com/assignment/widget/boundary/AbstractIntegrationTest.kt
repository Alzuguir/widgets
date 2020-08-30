package com.assignment.widget.boundary

import com.assignment.widget.WidgetApplication
import com.assignment.widget.adapter.memorystorage.WidgetMemoryStorage
import com.assignment.widget.adapter.repository.WidgetRepository
import com.google.gson.GsonBuilder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [WidgetApplication::class])
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var widgetRepository: WidgetRepository

    @MockBean
    lateinit var memoryStorage: WidgetMemoryStorage

    val gson = GsonBuilder().create()

}