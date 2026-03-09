package com.example.runcoachai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runcoachai.data.HourlyData

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                    HomeScreen()
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: WeatherViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.fetchWeather(52.41, 16.93, "Poznań")
    }

    Scaffold(
       topBar = {
           TopAppBar(
               title = {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Text(
                           text = "🏃‍♂️", // Zastępuje nam ikonkę
                           modifier = Modifier.padding(end = 8.dp) // Odstęp między ikonką a tekstem
                       )
                       Text(text = "RunCoach AI")
                   }
               },
               actions = {
                   IconButton(onClick = { println("Kliknięto kalendarz!") }){
                       Icon(
                           imageVector = Icons.Default.DateRange,
                           contentDescription = "Kalendarz"
                       )
                   }
                   IconButton(onClick = { println("Kliknięto dodawanie treningu!") }) {
                       Icon(
                           // Używamy nowej ikony zębatki
                           imageVector = Icons.Default.Add,
                           contentDescription = "Dodaj trening"
                       )
                   }
               }
           )
       }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.cityInput,
                onValueChange = { viewModel.cityInput = it },
                label = { Text("Wpisz miasto (np. Berlin, Tokyo)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchAndFetchWeather() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Search,
                            contentDescription = "Szukaj"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dzień dobry!",
                style = MaterialTheme.typography.headlineMedium,
            )

            WeatherCard(
                icon = viewModel.weatherIcon,
                description = viewModel.weatherDescription,
                temp = viewModel.temperature,
                cityName = viewModel.currentCityName
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Prognoza na najbliższe godziny",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Sprawdzamy, czy w ViewModelu są już dane godzinowe
            viewModel.hourlyForecast?.let { dane ->
                HourlyForecast(hourlyData = dane)
            } ?: run {
                // Jeśli jeszcze się ładują, pokaże się kręciołek
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun RunCoachScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

@Composable
fun WeatherCard(temp: String, icon: String, description: String, cityName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = cityName, style = MaterialTheme.typography.labelLarge)
                Text(text = temp, style = MaterialTheme.typography.displayMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = icon, style = MaterialTheme.typography.displayLarge)
        }
    }
}

@Composable
fun HourlyForecast(hourlyData: HourlyData) {
    // 1. Pobieramy obecną godzinę w formacie pasującym do API (np. "2026-03-09T14")
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH", java.util.Locale.getDefault())
    val currentFullHour = sdf.format(java.util.Date())

    // 2. Szukamy, na którym miejscu w liście jest ta godzina (szukamy dopasowania początku tekstu)
    val startIndex = hourlyData.time.indexOfFirst { it.startsWith(currentFullHour) }.coerceAtLeast(0)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Wyświetlamy 12 kolejnych godzin od obecnej
        items(12) { index ->
            val actualIndex = startIndex + index

            if (actualIndex < hourlyData.time.size) {
                val time = hourlyData.time[actualIndex].substringAfter("T")
                val temp = hourlyData.temperature_2m[actualIndex]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = time, style = MaterialTheme.typography.labelMedium)
                    Text(text = "${temp}°C", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}


