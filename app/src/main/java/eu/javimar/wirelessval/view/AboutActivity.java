/**
 * Wireless Valencia
 *
 * @author Javier Martín
 * @link http://www.javimar.eu (In construction)
 * @package eu.javimar.wirelessvlc
 * @version 1
 *
BSD 3-Clause License

Copyright (c) 2016, JaviMar
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

 * Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package eu.javimar.wirelessval.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.javimar.wirelessval.R;

public class AboutActivity extends AppCompatActivity
{
    private boolean full = false;
    @BindView(R.id.image_heart) ImageView heart;
    @BindView(R.id.textUpdated) TextView lastUpdated;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        ButterKnife.bind(this);

        // want to lock orientation in tablets to landscape only :-/
        if(getResources().getBoolean(R.bool.land_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setLastUpdated();
        // heart animation
        heart.setVisibility(View.VISIBLE);
        AnimatedVectorDrawable emptyHeart =
                (AnimatedVectorDrawable) AppCompatResources.getDrawable(this, R.drawable.avd_heart_empty);
        AnimatedVectorDrawable fillHeart =
                (AnimatedVectorDrawable) AppCompatResources.getDrawable(this, R.drawable.avd_heart_fill);

        AnimatedVectorDrawable drawable = full ? emptyHeart : fillHeart;
        heart.setImageDrawable(drawable);
        if (drawable != null) drawable.start();
        full = !full;
    }

    private void setLastUpdated() {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFERENCE_NAME", Context.MODE_PRIVATE);
        String updated = sharedPref.getString("LAST_UPDATED", "");
        lastUpdated.setText(getString(R.string.about_data_updated, updated));
    }
}