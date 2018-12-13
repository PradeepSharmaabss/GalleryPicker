package gallerypicker.pradeep.com.defaultgallerypicker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


/**
 * Pradeep
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var SELECT_MEDIA = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun openCamera() {
        Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, object : PermissionHandler() {
            override fun onGranted() {
                val mimeTypes = arrayOf(
                        "image/*", //images
                        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "video/*"//videos
                )
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
                    if (mimeTypes.size > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    }
                } else {
                    var mimeTypesStr = ""
                    for (mimeType in mimeTypes) {
                        mimeTypesStr += "$mimeType|"
                    }
                    intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
                }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                startActivityForResult(intent, SELECT_MEDIA)
            }
        })
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!!.clipData != null && data.clipData!!.itemCount != 0) {
            for (i in 0 until data.clipData!!.itemCount) {
                mediaPath.setText(RealPathUtils.getRealPath(this, data.clipData!!.getItemAt(i).uri))
            }
        } else if (data.getData() != null) {
            mediaPath.setText(RealPathUtils.getRealPath(this, data.getData()))
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
                openCamera()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
