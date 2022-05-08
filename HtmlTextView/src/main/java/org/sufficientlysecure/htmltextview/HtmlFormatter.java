/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.htmltextview;

import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HtmlFormatter {

    private HtmlFormatter() {
    }

    public static Spanned formatHtml(@NonNull final HtmlFormatterBuilder builder) {
        return formatHtml(
                builder.getHtml(), builder.getImageGetter(), builder.getClickableTableSpan(),
                builder.getDrawTableLinkSpan(), builder::getOnClickATagListener,
                builder.getOnClickImgListener(),
                builder.getIndent(),
                builder.isRemoveTrailingWhiteSpace()
        );
    }

    interface TagClickListenerProvider {
        OnClickATagListener provideTagClickListener();
    }

    public static Spanned formatHtml(@Nullable String html,
                                     ImageGetter imageGetter,
                                     ClickableTableSpan clickableTableSpan,
                                     DrawTableLinkSpan drawTableLinkSpan,
                                     TagClickListenerProvider tagClickListenerProvider,
                                     OnClickImgListener onClickImgListener,
                                     float indent,
                                     boolean removeTrailingWhiteSpace) {
        final HtmlTagHandler htmlTagHandler = new HtmlTagHandler();
        htmlTagHandler.setClickableTableSpan(clickableTableSpan);
        htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan);
        htmlTagHandler.setOnClickATagListenerProvider(tagClickListenerProvider);
        htmlTagHandler.setListIndentPx(indent);

        html = htmlTagHandler.overrideTags(html);

        Spanned formattedHtml;
        if (removeTrailingWhiteSpace) {
            formattedHtml = removeHtmlBottomPadding(Html.fromHtml(html, imageGetter, new WrapperContentHandler(htmlTagHandler)));
        } else {
            formattedHtml = Html.fromHtml(html, imageGetter, new WrapperContentHandler(htmlTagHandler));
        }

        if (null!=onClickImgListener&&null != formattedHtml) {
            if (formattedHtml instanceof Spannable){
                Spannable spannable = (Spannable) formattedHtml;

                ImageSpan[] spans = formattedHtml.getSpans(0, formattedHtml.length(), ImageSpan.class);
                for (int i = 0; i < spans.length; i++) {
                    ImageSpan span = spans[i];
                    int start = formattedHtml.getSpanStart(span);
                    int end = formattedHtml.getSpanEnd(span);
                    int finalI = i;

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            onClickImgListener.onClick(widget, finalI);
                        }
                    };


                    ClickableSpan[] click_spans = formattedHtml.getSpans(start, end, ClickableSpan.class);

                    if(click_spans.length != 0) {
                        // remove all click spans
                        for(ClickableSpan c_span : click_spans) {
                            spannable.removeSpan(c_span);
                        }
                    }

                    spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            }
        }

        return formattedHtml;
    }

    /**
     * Html.fromHtml sometimes adds extra space at the bottom.
     * This methods removes this space again.
     * See https://github.com/SufficientlySecure/html-textview/issues/19
     */
    @Nullable
    private static Spanned removeHtmlBottomPadding(@Nullable Spanned text) {
        if (text == null) {
            return null;
        }

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = (Spanned) text.subSequence(0, text.length() - 1);
        }
        return text;
    }
}
