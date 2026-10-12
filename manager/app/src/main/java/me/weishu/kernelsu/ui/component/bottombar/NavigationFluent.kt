package me.weishu.kernelsu.ui.component.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.repository.SettingsRepositoryImpl
import me.weishu.kernelsu.ui.LocalMainPagerState

private val fluentDestinations = listOf(
    R.string.home to Icons.Outlined.Home,
    R.string.superuser to Icons.Outlined.Shield,
    R.string.module to Icons.Outlined.Extension,
    R.string.settings to Icons.Outlined.Settings,
)

@Composable
fun BottomBarFluent(navigationBadge: NavigationBadgeState, modifier: Modifier = Modifier) {
    if (!Natives.isFullFeatured()) return
    val pager = LocalMainPagerState.current
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(
            modifier = Modifier.fillMaxWidth().windowInsetsPadding(
                WindowInsets.systemBars.union(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            ).padding(horizontal = 8.dp, vertical = 4.dp).selectableGroup(),
        ) {
            fluentDestinations.forEachIndexed { index, (label, icon) ->
                val selected = pager.selectedPage == index
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                        .background(if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
                        .selectable(selected, role = Role.Tab, onClick = { pager.animateToPage(index) })
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.width(20.dp).height(3.dp).background(
                        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(2.dp)
                    ))
                    Spacer(Modifier.height(4.dp))
                    NavigationIconWithBadge(icon, stringResource(label), badgeFor(index, navigationBadge))
                    Text(stringResource(label), style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun NavigationRailFluent(navigationBadge: NavigationBadgeState, modifier: Modifier = Modifier) {
    if (!Natives.isFullFeatured()) return
    val pager = LocalMainPagerState.current
    val repo = remember { SettingsRepositoryImpl() }
    var expanded by remember { mutableStateOf(repo.navigationRailExpanded) }
    Surface(modifier = modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
            modifier = Modifier.width(if (expanded) 220.dp else 80.dp).windowInsetsPadding(
                WindowInsets.systemBars.union(WindowInsets.displayCutout).only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)
            ).padding(8.dp).selectableGroup(),
        ) {
            IconButton(onClick = { expanded = !expanded; repo.navigationRailExpanded = expanded }) {
                Icon(Icons.Filled.Menu, stringResource(if (expanded) R.string.nav_rail_collapse else R.string.nav_rail_expand))
            }
            fluentDestinations.forEachIndexed { index, (label, icon) ->
                val selected = pager.selectedPage == index
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(6.dp))
                        .background(if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
                        .selectable(selected, role = Role.Tab, onClick = { pager.animateToPage(index) })
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(3.dp).height(20.dp).background(
                        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(2.dp)
                    ))
                    Spacer(Modifier.width(8.dp))
                    NavigationIconWithBadge(icon, stringResource(label), badgeFor(index, navigationBadge))
                    if (expanded) {
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(label), style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
