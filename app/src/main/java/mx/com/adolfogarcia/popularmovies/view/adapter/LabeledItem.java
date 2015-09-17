/*
 * Copyright 2015 Jesús Adolfo García Pasquel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.com.adolfogarcia.popularmovies.view.adapter;

/**
 * Associates a label to an object. {@link #toString()} returns the value of
 * the label, making this class useful as a wrapper for entries in
 * {@link android.widget.ArrayAdapter}, with uses that value to represent
 * its entries.
 *
 * @param <T> The type of the item being labeled.
 * @author Jesús Adolfo García Pasquel
 */
public class LabeledItem<T> {

    /**
     * The object to which the label is associated.
     */
    private final T mItem;

    /**
     * The label.
     */
    private final String mLabel;

    /**
     * Creates a new instance of {@link LabeledItem}.
     *
     * @param label the label.
     * @param item the object to which the label is associated.
     */
    public LabeledItem(String label, T item) {
        mItem = item;
        mLabel = label;
    }

    public T getItem() {
        return mItem;
    }

    /**
     * Returns the label, just as {@link #toString()}.
     *
     * @return the label, just as {@link #toString()}.
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Returns the label, just as {@link #getLabel()}.
     *
     * @return the label, just as {@link #getLabel()}.
     */
    @Override
    public String toString() {
        return mLabel;
    }

}
