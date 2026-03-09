package com.example.runcoachai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
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
    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    androidx.compose.ui.graphics.Color(0xFF82B1FF), // Jasny błękit
                    androidx.compose.ui.graphics.Color(0xFF448AFF)  // Ciemniejszy błękit
                )
            )
        )) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
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
                        IconButton(onClick = { println("Kliknięto kalendarz!") }) {
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent, // Przezroczysty pasek
                        titleContentColor = androidx.compose.ui.graphics.Color.White,    // Biały tytuł
                        actionIconContentColor = androidx.compose.ui.graphics.Color.White // Białe ikonki
                    )
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color.White, // Ramka po kliknięciu
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f), // Ramka przed kliknięciem
                        focusedLabelColor = androidx.compose.ui.graphics.Color.White, // Tekst etykiety po kliknięciu
                        unfocusedLabelColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), // Tekst etykiety przed
                        focusedTextColor = androidx.compose.ui.graphics.Color.White, // Wpisywany tekst
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        cursorColor = androidx.compose.ui.graphics.Color.White, // Kursor
                        focusedTrailingIconColor = androidx.compose.ui.graphics.Color.White, // LUPKA po kliknięciu
                        unfocusedTrailingIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f) // LUPKA przed kliknięciem
                    ),
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
                    color = androidx.compose.ui.graphics.Color.White
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
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = androidx.compose.ui.graphics.Color.White
                )

                // Sprawdzamy, czy w ViewModelu są już dane godzinowe
                viewModel.hourlyForecast?.let { dane ->
                    HourlyForecast(hourlyData = dane, viewModel = viewModel)
                } ?: run {
                    // Jeśli jeszcze się ładują, pokaże się kręciołek
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = androidx.compose.ui.graphics.Color.White
                    )

                }
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
    // 1. GŁÓWNY KONTENER - ROW (układa elementy poziomo obok siebie)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Rozsuwa tekst i ikonę na boki
        verticalAlignment = Alignment.CenterVertically // Wyśrodkowuje je w pionie
    ) {

        // 2. LEWA STRONA - KOLUMNA (układa teksty pionowo pod sobą)
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.headlineSmall,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                text = temp,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
            )
        }

        // 3. PRAWA STRONA - IKONKA (stoi obok Kolumny, bo są razem w Row)
        Text(
            text = icon,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
        )
    }
}

@Composable
fun HourlyForecast(hourlyData: HourlyData, viewModel: WeatherViewModel) {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH", java.util.Locale.getDefault())
    val currentFullHour = sdf.format(java.util.Date())
    val startIndex = hourlyData.time.indexOfFirst { it.startsWith(currentFullHour) }.coerceAtLeast(0)

    // EFEKT SZKŁA (Glassmorphism)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), // Półprzezroczysta biel
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // Zaokrąglone rogi
    ) {
        LazyRow(
            contentPadding = PaddingValues(16.dp), // Odstęp wewnątrz ramki
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(12) { index ->
                val actualIndex = startIndex + index

                if (actualIndex < hourlyData.time.size) {
                    val time = hourlyData.time[actualIndex].substringAfter("T")
                    val temp = hourlyData.temperature_2m[actualIndex]

                    val code = hourlyData.weathercode[actualIndex]
                    val icon = viewModel.getWeatherIcon(code)

                    val precipProb = hourlyData.precipitation_probability?.get(actualIndex) ?: 0

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Zmieniamy kolory na białe!
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = icon, fontSize = 24.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${temp}°C",
                            style = MaterialTheme.typography.bodyLarge,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💧", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$precipProb%",
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}


