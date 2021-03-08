package org.lucaji.pianotoner;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dci.dev.appinfobadge.AppInfoBadge

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
       // setSupportActionBar(findViewById(R.id.toolbar))

        //findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
        //}

        val fragment = AppInfoBadge
                .darkMode { true }
                .withAppIcon { true }
                .headerColor { 0xE53935}
                .withPermissions { true }
                .withChangelog { true }
                .withLicenses { true }
                .withLibraries { true }
                .withRater { true }
                .withEmail { "lucaji@mail.ru" }
                .withSite { "https://lucaji.github.io" }
                .show()


        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit()
    }
}