package com.example.tugas_5_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugas_5_calculator.ui.theme.Tugas5calculatorTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tugas5calculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf("") }
    var previousInput by remember { mutableStateOf("0") }
    var clearNext by remember { mutableStateOf(false) }
    val formatter = remember { DecimalFormat("#,###.########") }

    fun formatNumber(number: String): String {
        return try {
            if (number.contains(".")) {
                number
            } else {
                formatter.format(number.toDouble())
            }
        } catch (e: Exception) {
            number
        }
    }

    fun calculate(): String {
        return try {
            val num1 = previousInput.replace(",", "").toDouble()
            val num2 = input.replace(",", "").toDouble()
            
            val result = when (operation) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.POSITIVE_INFINITY
                "%" -> num1 % num2
                else -> num2
            }
            
            if (result.isInfinite() || result.isNaN()) {
                "Error"
            } else if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                result.toString()
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    fun handleNumber(number: String) {
        input = if (input == "0" || clearNext) {
            clearNext = false
            number
        } else if (input == "Error") {
            number
        } else if (input.length < 15) { // Limit input length
            input + number
        } else {
            input
        }
    }

    fun handleOperation(op: String) {
        if (input != "Error") {
            when {
                operation.isEmpty() -> {
                    operation = op
                    previousInput = input
                    clearNext = true
                }
                clearNext -> {
                    operation = op
                }
                else -> {
                    // Perform calculation and prepare for next operation
                    val result = calculate()
                    if (result != "Error") {
                        input = result
                        previousInput = result
                        operation = op
                        clearNext = true
                    } else {
                        input = "Error"
                        operation = ""
                        previousInput = "0"
                    }
                }
            }
        }
    }

    fun handleEquals() {
        if (operation.isNotEmpty() && input != "Error") {
            val result = calculate()
            input = result
            previousInput = "0"
            operation = ""
            clearNext = true
        }
    }

    fun handleClear() {
        input = "0"
        operation = ""
        previousInput = "0"
        clearNext = false
    }

    fun handleDelete() {
        if (input.length > 1) {
            input = input.dropLast(1)
        } else {
            input = "0"
        }
    }

    fun handleDecimal() {
        if (!input.contains(".") && input != "Error") {
            input = "$input."
        }
    }

    fun handleNegate() {
        if (input != "0" && input != "Error") {
            input = if (input.startsWith("-")) {
                input.substring(1)
            } else {
                "-$input"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (operation.isNotEmpty()) "${formatNumber(previousInput)} $operation" else "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = formatNumber(input),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Button rows
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // First row: Clear, Delete, Percentage, Divide
                CalculatorButtonRow {
                    FunctionButton(text = "AC", onClick = { handleClear() })
                    FunctionButton(text = "⌫", onClick = { handleDelete() })
                    FunctionButton(text = "%", onClick = { handleOperation("%") })
                    OperatorButton(text = "÷", onClick = { handleOperation("÷") })
                }
                
                // Second row: 7, 8, 9, Multiply
                CalculatorButtonRow {
                    NumberButton(text = "7", onClick = { handleNumber("7") })
                    NumberButton(text = "8", onClick = { handleNumber("8") })
                    NumberButton(text = "9", onClick = { handleNumber("9") })
                    OperatorButton(text = "×", onClick = { handleOperation("×") })
                }
                
                // Third row: 4, 5, 6, Subtract
                CalculatorButtonRow {
                    NumberButton(text = "4", onClick = { handleNumber("4") })
                    NumberButton(text = "5", onClick = { handleNumber("5") })
                    NumberButton(text = "6", onClick = { handleNumber("6") })
                    OperatorButton(text = "-", onClick = { handleOperation("-") })
                }
                
                // Fourth row: 1, 2, 3, Add
                CalculatorButtonRow {
                    NumberButton(text = "1", onClick = { handleNumber("1") })
                    NumberButton(text = "2", onClick = { handleNumber("2") })
                    NumberButton(text = "3", onClick = { handleNumber("3") })
                    OperatorButton(text = "+", onClick = { handleOperation("+") })
                }
                
                // Fifth row: +/-, 0, ., =
                CalculatorButtonRow {
                    FunctionButton(text = "±", onClick = { handleNegate() })
                    NumberButton(text = "0", onClick = { handleNumber("0") })
                    FunctionButton(text = ".", onClick = { handleDecimal() })
                    EqualsButton(text = "=", onClick = { handleEquals() })
                }
            }
        }
    }
}

@Composable
fun CalculatorButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
fun RowScope.NumberButton(text: String, onClick: () -> Unit) {
    CalculatorButton(
        text = text,
        backgroundColor = Color(0xFF303030),
        textColor = Color.White,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun RowScope.OperatorButton(text: String, onClick: () -> Unit) {
    CalculatorButton(
        text = text,
        backgroundColor = Color(0xFFF57C00),
        textColor = Color.White,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun RowScope.FunctionButton(text: String, onClick: () -> Unit) {
    CalculatorButton(
        text = text,
        backgroundColor = Color(0xFF757575),
        textColor = Color.White,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun RowScope.EqualsButton(text: String, onClick: () -> Unit) {
    CalculatorButton(
        text = text,
        backgroundColor = Color(0xFFF57C00),
        textColor = Color.White,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    Tugas5calculatorTheme {
        CalculatorApp()
    }
}