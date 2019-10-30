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

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import eu.javimar.wirelessval.R;


public class FragmentAbout extends Fragment
{
    private boolean full = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.about_layout, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        TextView title = getActivity().findViewById(R.id.textTitle);
        TextView copy = getActivity().findViewById(R.id.textAboutVersion);
        TextView info = getActivity().findViewById(R.id.textAboutInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            // heart animation
            ImageView heart = getActivity().findViewById(R.id.image_heart);
            heart.setVisibility(View.VISIBLE);
            AnimatedVectorDrawable emptyHeart =
                    (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_heart_empty);
            AnimatedVectorDrawable fillHeart =
                    (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_heart_fill);

            AnimatedVectorDrawable drawable = full ? emptyHeart : fillHeart;
            heart.setImageDrawable(drawable);
            if (drawable != null) {
                drawable.start();
            }
            full = !full;
        }

        title.setText(getString(R.string.about_info_name));
        copy.setText(getString(R.string.about_info_copy));
        info.setText(getString(R.string.about_info_text));
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // calls to FragmentTransaction are asynchronous, this gives a NPE
        // since MainActivity won't be attached by the time commit() returns
        getActivity().setTitle(getString(R.string.title_about_activity));
    }

}