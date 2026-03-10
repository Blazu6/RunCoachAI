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
    // Zmienna do okienka dodawania treningu (jeśli już ją masz, to zostaw)
    var showDialog by remember { mutableStateOf(false) }

    // NOWE ZMIENNE DLA KALENDARZA
    var showCalendar by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                        IconButton(onClick = { showCalendar = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Kalendarz"
                            )
                        }
                        IconButton(onClick = { showDialog = true }) {
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
                Spacer(modifier = Modifier.height(24.dp))

                // KARTA TRENERA AI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🤖", // Ikonka AI / Mózgu
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                        Text(
                            text = viewModel.aiRecommendation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = androidx.compose.ui.graphics.Color.White, // Ciemny tekst na jasnej karcie
                            lineHeight = 24.sp
                        )
                    }
                }
                // 1. WYWOŁUJEMY CZYSTY KALENDARZ
                RunCalendarDialog(
                    showCalendar = showCalendar,
                    onDismiss = { showCalendar = false },
                    onDateSelected = { dataInMillis ->
                        println("Wybrana data: $dataInMillis")
                        showCalendar = false
                    }
                )

                // 2. WYWOŁUJEMY CZYSTE DODAWANIE TRENINGU
                AddWorkoutDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onSave = { dystans ->
                        // Tutaj wysyłamy do ViewModela, żeby zapisał!
                        println("Trening: $dystans km")
                        showDialog = false
                    }
                )
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
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunCalendarDialog(
    showCalendar: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit
) {
    if (!showCalendar) return // Jeśli false, w ogóle tego nie rysujemy

    val datePickerState = rememberDatePickerState()

    val dialogBackgroundColor = androidx.compose.ui.graphics.Color(0xFF64B5F6)

    val customDatePickerColors = DatePickerDefaults.colors(
        // 1. NAPRAWA NAKŁADANIA: Zamiast Transparent, używamy pełnego koloru
        containerColor = dialogBackgroundColor,

        // 2. NAPRAWA CZARNYCH NAPISÓW: Wszystko na biało
        titleContentColor = androidx.compose.ui.graphics.Color.White,
        headlineContentColor = androidx.compose.ui.graphics.Color.White,
        weekdayContentColor = androidx.compose.ui.graphics.Color.White,
        subheadContentColor = androidx.compose.ui.graphics.Color.White, // To jest "March 2026"
        navigationContentColor = androidx.compose.ui.graphics.Color.White, // To są strzałki

        yearContentColor = androidx.compose.ui.graphics.Color.White,
        currentYearContentColor = androidx.compose.ui.graphics.Color.White,
        selectedYearContentColor = dialogBackgroundColor, // Niebieski tekst roku, bo kółko będzie białe
        selectedYearContainerColor = androidx.compose.ui.graphics.Color.White,

        dayContentColor = androidx.compose.ui.graphics.Color.White,
        disabledDayContentColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
        selectedDayContentColor = dialogBackgroundColor, // Niebieski tekst dnia
        selectedDayContainerColor = androidx.compose.ui.graphics.Color.White, // Białe kółko wokół wybranego dnia
        todayContentColor = androidx.compose.ui.graphics.Color.White,
        todayDateBorderColor = androidx.compose.ui.graphics.Color.White
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = dialogBackgroundColor // Możesz tu dać kolor ze swojego gradientu
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp), // Mocne zaokrąglenie, jak w naszej pogodzie
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(datePickerState.selectedDateMillis) }
            ) {
                Text("Wybierz", color = androidx.compose.ui.graphics.Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides androidx.compose.ui.graphics.Color.White
        ) {
            DatePicker(
                state = datePickerState,
                colors = customDatePickerColors,
                showModeToggle = false // Opcjonalnie: ukrywa ikonkę ołówka, żeby zostawić tylko widok siatki
            )
        }
    }
}

@Composable
fun AddWorkoutDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit // Zwracamy wpisany tekst na zewnątrz!
) {
    if (!showDialog) return

    var kmInput by remember { mutableStateOf("") }

    val dialogBackgroundColor = androidx.compose.ui.graphics.Color(0xFF64B5F6)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBackgroundColor,
        titleContentColor = androidx.compose.ui.graphics.Color.White,
        textContentColor = androidx.compose.ui.graphics.Color.White,
        iconContentColor = androidx.compose.ui.graphics.Color.White,
        title = { Text("Dodaj dzisiejszy trening 🏃‍♂️") },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        text = {
            Column {
                Text("Ile kilometrów dzisiaj przebiegłeś?")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = kmInput,
                    onValueChange = { kmInput = it },
                    label = { Text("Dystans (km)", color = androidx.compose.ui.graphics.Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f),
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        cursorColor = androidx.compose.ui.graphics.Color.White
                    ),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(kmInput) // Wysyłamy dystans "wyżej"
                kmInput = ""    // Czyścimy pole
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.White,
                contentColor = dialogBackgroundColor)
            ) {
                Text("Zapisz", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj", color = androidx.compose.ui.graphics.Color.White) }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddWorkoutDialogPreview() {
    MaterialTheme {
        AddWorkoutDialog(
            showDialog = true, // Wymuszamy pokazanie okienka w podglądzie
            onDismiss = {},    // Pusta akcja (nic nie rób)
            onSave = {}        // Pusta akcja
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RunCalendarDialogPreview() {
    MaterialTheme {
        RunCalendarDialog(
            showCalendar = true, // Wymuszamy pokazanie kalendarza
            onDismiss = {},
            onDateSelected = {}
        )
    }
}


