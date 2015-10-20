package com.habitrpg.android.habitica;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.habitrpg.android.habitica.events.ToggledInnStateEvent;
import com.habitrpg.android.habitica.prefs.PrefsActivity;
import com.habitrpg.android.habitica.ui.AvatarWithBarsViewModel;
import com.habitrpg.android.habitica.ui.MainDrawerBuilder;
import com.habitrpg.android.habitica.ui.fragments.ChatListFragment;
import com.magicmicky.habitrpgwrapper.lib.models.HabitRPGUser;
import com.mikepenz.materialdrawer.Drawer;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class TavernActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.avatar)
    LinearLayout avatarHeader;

    private AvatarWithBarsViewModel avatarInHeader;
    private APIHelper mAPIHelper;
    private HabitRPGUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tavern);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        // Receive Events
        EventBus.getDefault().register(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);

            toolbar.setTitleTextColor(this.getResources().getColor(R.color.white));
        }

        Drawer drawer = MainDrawerBuilder.CreateDefaultBuilderSettings(this, toolbar)
                .withTranslucentNavigationBar(false)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(false)
                .withSelectedItem(2)
                .build();

        avatarInHeader = new AvatarWithBarsViewModel(this, avatarHeader);

        HostConfig hostConfig = PrefsActivity.fromContext(this);
        User = new Select().from(HabitRPGUser.class).where(Condition.column("id").eq(hostConfig.getUser())).querySingle();

        avatarInHeader.UpdateData(User);

        mAPIHelper = new APIHelper(this, hostConfig);

        setFragment(new ChatListFragment(this, "habitrpg", mAPIHelper, User, true));
    }

    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tavern_framelayout, fragment);
        fragmentTransaction.commit();
    }

    public void onEvent(ToggledInnStateEvent evt){
        avatarInHeader.UpdateData(User);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
