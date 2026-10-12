// ==================================================
// FILE: MainScaffold.kt
// VERSION: 2.1.0
// PURPOSE: Root Layout with Modal Navigation Drawer (Compose Only)
// NOTE: Replaces activity_main.xml and side_menu.xml completely.
// ==================================================
package com.martodosko.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martodosko.studio.ui.screens.home.PedalBoardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier
) {
    var drawerOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Martodosko Studio") },
                navigationIcon = {
                    IconButton(onClick = { drawerOpen = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        
        // Content Area (The Pedal Board)
        Box(modifier = Modifier.padding(paddingValues)) {
            PedalBoardScreen()
        }

        // Modal Navigation Drawer (Side Menu)
        if (drawerOpen) {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // Header
                    Text(
                        text = "NAVIGATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    
                    HorizontalDivider()
                    
                    // Menu Items
                    NavItem(text = "🏠 Home", onClick = { drawerOpen = false })
                    NavItem(text = "🎚️ Mixer", onClick = { /* Navigate to Mixer */ })
                    NavItem(text = "📋 Presets", onClick = { /* Navigate to Presets */ })
                    NavItem(text = "⚙️ Settings", onClick = { /* Navigate to Settings */ })
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Footer/Legal
                    TextButton(onClick = { /* Open Legal */ }) {
                        Text("Terms & Privacy", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            
            // Backdrop click handler
            Scrim(onDismiss = { drawerOpen = false })
        }
    }
}

@Composable
fun NavItem(text: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text) },
        leadingContent = { /* Add Icon here if desired */ },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

// Helper for clickable modifier if not imported
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.remember
