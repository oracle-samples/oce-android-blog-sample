/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.oracle.content.samples.supremo.R

class ErrorFragment : Fragment() {

    companion object {
        const val ERROR_MSG_KEY = "errorMsg"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_error, container, false)

        val errorMessage = arguments?.getString(ERROR_MSG_KEY)

        view.findViewById<TextView>(R.id.error_message).text = errorMessage

        return view
    }

}