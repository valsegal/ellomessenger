/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 * Copyright Nikita Denin, Ello 2024.
 */
package org.telegram.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FeedWidgetProvider;

public class FeedWidgetConfigActivity extends ExternalActionActivity {
	private int creatingAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
	protected boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
		if (!checkPasscode(intent, isNew, restore, fromPassword, intentAccount, state)) {
			return false;
		}
		Bundle extras = intent.getExtras();
		if (extras != null) {
			creatingAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (creatingAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			Bundle args = new Bundle();
			args.putBoolean("onlySelect", true);
			args.putInt("dialogsType", 5);
			args.putBoolean("allowSwitchAccount", true);
			args.putBoolean("checkCanWrite", false);
			DialogsActivity fragment = new DialogsActivity(args);
			fragment.setDelegate((fragment1, dids, message, param) -> {
				AccountInstance.getInstance(fragment1.getCurrentAccount()).getMessagesStorage().putWidgetDialogs(creatingAppWidgetId, dids);

				SharedPreferences preferences = FeedWidgetConfigActivity.this.getSharedPreferences("shortcut_widget", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("account" + creatingAppWidgetId, fragment1.getCurrentAccount());
				editor.putLong("dialogId" + creatingAppWidgetId, dids.get(0));
				editor.commit();

				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(FeedWidgetConfigActivity.this);
				FeedWidgetProvider.updateWidget(FeedWidgetConfigActivity.this, appWidgetManager, creatingAppWidgetId);

				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, creatingAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			});

			if (AndroidUtilities.isTablet()) {
				if (layersActionBarLayout.fragmentsStack.isEmpty()) {
					layersActionBarLayout.addFragmentToStack(fragment);
				}
			}
			else {
				if (actionBarLayout.fragmentsStack.isEmpty()) {
					actionBarLayout.addFragmentToStack(fragment);
				}
			}
			if (!AndroidUtilities.isTablet()) {
				backgroundTablet.setVisibility(View.GONE);
			}
			actionBarLayout.showLastFragment();
			if (AndroidUtilities.isTablet()) {
				layersActionBarLayout.showLastFragment();
			}
			intent.setAction(null);
		}
		else {
			finish();
		}
		return true;
	}
}