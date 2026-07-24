package com.mensinator.app.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mensinator.app.R
import com.mensinator.app.ui.navigation.displayCutoutExcludingStatusBarsPadding
import com.mensinator.app.ui.theme.MensinatorTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = koinViewModel(),
) {
    val state = viewModel.viewState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    StatisticsScreenContent(modifier, state)
}

@Composable
private fun StatisticsScreenContent(
    modifier: Modifier = Modifier,
    state: StatisticsViewModel.ViewState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .displayCutoutExcludingStatusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        
        // 🚨 Safe Null-Checked Irregularity Diagnostic Health Card
        val rawTracked = state.trackedPeriods
        val totalPeriodsTracked = rawTracked?.toIntOrNull() ?: 0
        val cleanCycleString = state.averageCycleLength ?: ""
        
        if (totalPeriodsTracked >= 3 && (cleanCycleString.contains("varies") || cleanCycleString.contains("irregular") || totalPeriodsTracked > 5)) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFFFEBF0)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ℹ️ " + stringResource(id = R.string.irregular_cycle_warning_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color(0xFFE07A8D),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(id = R.string.irregular_cycle_warning_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF2C3E50)
                    )
                }
            }
        }

        // Standard metrics fields render smoothly below
        RowOfText(
            stringResource(id = R.string.period_count),
            state.trackedPeriods
        )
        RowOfText(
            stringResource(id = R.string.average_cycle_length),
            state.averageCycleLength
        )
        RowOfText(
            stringResource(id = R.string.average_period_length),
            state.averagePeriodLength
        )
        RowOfText(
            stringResource(id = R.string.next_period_start_future),
            state.periodPredictionDate
        )
        RowOfText(
            stringResource(id = R.string.ovulation_count),
            state.ovulationCount
        )
        RowOfText(
            stringResource(id = R.string.average_ovulation_day),
            state.follicleGrowthDays
        )
        RowOfText(
            stringResource(id = R.string.next_predicted_ovulation),
            state.ovulationPredictionDate
        )
        RowOfText(
            stringResource(id = R.string.average_luteal_length),
            state.averageLutealLength
        )
    }
}

@Composable
fun RowOfText(stringOne: String, stringTwo: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceAround
    ) {
        Text(
            text = stringOne,
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 8.dp),
        )
        stringTwo?.let {
            Text(
                text = it,
                modifier = Modifier.alignByBaseline().widthIn(max = 200.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RowOfTextPreview() {
    RowOfText("firstString", "secondstring")
}

@Preview(showBackground = true)
@Composable
private fun StatisticsScreenPreview() {
    MensinatorTheme {
        StatisticsScreenContent(
            state = StatisticsViewModel.ViewState(
                trackedPeriods = "3",
                averageCycleLength = "32.0 days (irregular variance)",
                averagePeriodLength = "5.0 days",
                periodPredictionDate = "28 Feb 2024",
                ovulationCount = "4",
                ovulationPredictionDate = "20 Mar 2024",
                follicleGrowthDays = "14.0",
                averageLutealLength = "15.0 days"
            )
        )
    }
}
