<?xml version="1.0" encoding="utf-8"?>
<!-- ================================================== -->
<!-- FILE: activity_admin_panel.xml — ✅ BUONG ADMIN PANEL UI! -->
<!-- VERSION: 1.0.0 — 👑 OWNER + 🔐 ADMIN — TUGMA SA HIERARCHY! -->
<!-- UPDATED: 2026-09-20 -->
<!-- ================================================== -->
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#0F0F1A">

    <!-- ✅ PANGUNAHING LAMAN -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- ✅ TOP BAR → PAMAGAT + ANTAS NG USER -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="#1A1A30"
            android:padding="16dp"
            android:layout_marginBottom="16dp"
            android:elevation="4dp">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="🔐 ADMIN PANEL"
                android:textSize="22sp"
                android:textStyle="bold"
                android:textColor="#FFD700"
                android:gravity="center"/>

            <TextView
                android:id="@+id/admin_access_level"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="— NAGKAKARUG NG KARAPATAN —"
                android:textSize="14sp"
                android:gravity="center"
                android:layout_marginTop="4dp"/>
        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

                <!-- ============================================== -->
                <!-- 📊 SECTION 1 — ESTATISTIKA -->
                <!-- ============================================== -->
                <LinearLayout
                    android:id="@+id/section_statistics"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:background="#151528"
                    android:padding="16dp"
                    android:layout_marginBottom="12dp">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📊 BUOD NG PROYEKTO"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="#40E0D0"
                        android:layout_marginBottom="12dp"/>

                    <GridLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:columnCount="2"
                        android:rowCount="2">

                        <TextView android:id="@+id/tv_stat_keys" android:text="Likhang Key Code: 0" android:textColor="#CCC" android:padding="8dp"/>
                        <TextView android:id="@+id/tv_stat_members" android:text="Miyembro: 0" android:textColor="#CCC" android:padding="8dp"/>
                        <TextView android:id="@+id/tv_stat_presets" android:text="Kabuuang Preset: 0" android:textColor="#CCC" android:padding="8dp"/>
                        <TextView android:id="@+id/tv_stat_pending" android:text="Nakabinbin: 0" android:textColor="#CCC" android:padding="8dp"/>
                    </GridLayout>
                </LinearLayout>

                <!-- ============================================== -->
                <!-- 👑 SECTION 2 — KEY CODE GENERATOR — OWNER LANG! -->
                <!-- ============================================== -->
                <LinearLayout
                    android:id="@+id/section_key_generator"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:background="#1A1528"
                    android:padding="16dp"
                    android:layout_marginBottom="12dp"
                    android:visibility="gone">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="👑 KEY CODE GENERATOR — OWNER LANG!"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="#FFD700"
                        android:layout_marginBottom="12dp"/>

                    <Button
                        android:id="@+id/btn_generate_key"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="✅ LIKHANG BAGONG KEY CODE"
                        android:background="#9C27B0"
                        android:textColor="#FFFFFF"
                        android:paddingVertical="12dp"
                        android:layout_marginBottom="8dp"/>

                    <Button
                        android:id="@+id/btn_clear_all_keys"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="🗑️ BURAHIN LAHAT NG KEY CODE"
                        android:background="#CF2727"
                        android:textColor="#FFFFFF"
                        android:paddingVertical="10dp"
                        android:layout_marginBottom="12dp"/>

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Mga Nalikhang Key Code:"
                        android:textSize="13sp"
                        android:textColor="#888"
                        android:layout_marginBottom="4dp"/>

                    <TextView
                        android:id="@+id/tv_generated_keys"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Wala pang nalikhang Key Code."
                        android:textSize="13sp"
                        android:textColor="#CCC"
                        android:fontFamily="monospace"
                        android:padding="10dp"
                        android:background="#222240"/>
                </LinearLayout>

                <!-- ============================================== -->
                <!-- 🔐 SECTION 3 — USER MANAGEMENT -->
                <!-- ============================================== -->
                <LinearLayout
                    android:id="@+id/section_user_management"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:background="#152028"
                    android:padding="16dp"
                    android:layout_marginBottom="12dp"
                    android:visibility="gone">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="👤 PAMAHALAAN NG MIYEMBRO"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="#03DAC6"
                        android:layout_marginBottom="12dp"/>

                    <Button
                        android:id="@+id/btn_refresh_users"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="🔄 I-REFRESH ANG LISTAHAN"
                        android:background="#2563EB"
                        android:textColor="#FFFFFF"
                        android:paddingVertical="10dp"
                        android:layout_marginBottom="12dp"/>

                    <TextView
                        android:id="@+id/tv_user_list"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Wala pang rehistradong miyembro."
                        android:textSize="13sp"
                        android:textColor="#CCC"
                        android:padding="10dp"
                        android:background="#1A2C38"/>
                </LinearLayout>

                <!-- ============================================== -->
                <!-- 📋 SECTION 4 — PRESET MODERATION -->
                <!-- ============================================== -->
                <LinearLayout
                    android:id="@+id/section_preset_moderation"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:background="#1A2015"
                    android:padding="16dp"
                    android:layout_marginBottom="12dp"
                    android:visibility="gone">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📋 PAGSUSURI NG PRESET"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="#FFC107"
                        android:layout_marginBottom="12dp"/>

                    <Button
                        android:id="@+id/btn_refresh_presets"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="🔄 I-REFRESH ANG LISTAHAN"
                        android:background="#F59E0B"
                        android:textColor="#000000"
                        android:paddingVertical="10dp"
                        android:layout_marginBottom="12dp"/>

                    <TextView
                        android:id="@+id/tv_preset_list"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Walang nakabinbing preset."
                        android:textSize="13sp"
                        android:textColor="#CCC"
                        android:padding="10dp"
                        android:background="#2C2A15"/>
                </LinearLayout>

                <!-- ============================================== -->
                <!-- ℹ️ IMPORMASYON SA IBABA -->
                <!-- ============================================== -->
                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="💡 Tandaan: Ang Admin Panel ay para sa pamamahala ng proyekto. Ang Member System ay para sa pagpapalawak ng komunidad — hindi paghihigpit. Ang app ay mananatiling libre para sa lahat."
                    android:textSize="12sp"
                    android:textColor="#666"
                    android:padding="12dp"
                    android:layout_marginTop="8dp"/>

            </LinearLayout>
        </ScrollView>
    </LinearLayout>
</androidx.drawerlayout.widget.DrawerLayout>
