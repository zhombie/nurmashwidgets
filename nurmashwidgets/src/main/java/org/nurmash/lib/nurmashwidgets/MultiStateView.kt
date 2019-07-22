package org.nurmash.lib.nurmashwidgets

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

/**
 * View that contains 4 different states: [State.Content], [State.Error], [State.Empty],
 * & [State.Loading]. Each state has their own separate layout which can be shown/hidden by
 * the [State] accordingly. Every [MultiStateView] <b><i>MUST</i></b> contain a [contentView].
 * The [contentView] is obtained from whatever is inside of the tags of the view via its XML declaration
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class MultiStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleAttr) {

    companion object {
        sealed class State {
            annotation class MultiStateViewState

            object Unknown : State()
            object Content : State()
            data class Error(val message: String) : State()
            object Empty : State()
            object Loading : State()
        }

        /**
         * Be careful! These [Integer] values, which are used to manage view types,
         * *MUST* be same as values from [MultiStateView] defaultViewState attrs enum values,
         * see file attrs.xml in resources directory (MultiStateView styleable)
         */
        private const val VIEW_STATE_UNKNOWN = -1
        private const val VIEW_STATE_CONTENT = 0
        private const val VIEW_STATE_ERROR = 1
        private const val VIEW_STATE_EMPTY = 2
        private const val VIEW_STATE_LOADING = 3

        /**
         * Default views for [State.Error], [State.Empty], [State.Loading] by alphabetical order
         */
        @LayoutRes
        private val DEFAULT_VIEW_STATE_EMPTY_LAYOUT_ID = R.layout.default_state_empty_view

        @LayoutRes
        private val DEFAULT_VIEW_STATE_ERROR_LAYOUT_ID = R.layout.default_state_error_view

        @LayoutRes
        private val DEFAULT_VIEW_STATE_LOADING_LAYOUT_ID = R.layout.default_state_loading_view

    }

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private var contentView: View? = null
    private var loadingView: View? = null
    private var errorView: View? = null
    private var emptyView: View? = null

    private var listener: StateListener? = null
    private var emptyStateActionListener: EmptyStateActionListener? = null
    private var errorStateActionListener: ErrorStateActionListener? = null


    @State.MultiStateViewState
    private var viewState: State = State.Unknown

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiStateView)

        /**
         * Loading view initialization
         */
        val loadingViewResId = typedArray.getResourceId(R.styleable.MultiStateView_loadingView, -1)
        val loadingView = if (loadingViewResId > -1) {
            layoutInflater.inflate(loadingViewResId, this, false)
        } else {
            layoutInflater.inflate(DEFAULT_VIEW_STATE_LOADING_LAYOUT_ID, this, false)
        }
        this.loadingView = loadingView
        addView(loadingView, loadingView.layoutParams)

        /**
         * Empty view initialization
         */
        val emptyViewResId = typedArray.getResourceId(R.styleable.MultiStateView_emptyView, -1)
        val emptyView = if (emptyViewResId > -1) {
            layoutInflater.inflate(emptyViewResId, this, false)
        } else {
            layoutInflater.inflate(DEFAULT_VIEW_STATE_EMPTY_LAYOUT_ID, this, false)
        }
        this.emptyView = emptyView
        addView(emptyView, emptyView.layoutParams)

        /**
         * Error view initialization
         */
        val errorViewResId = typedArray.getResourceId(R.styleable.MultiStateView_errorView, -1)
        val errorView = if (errorViewResId > -1) {
            layoutInflater.inflate(errorViewResId, this, false)
        } else {
            layoutInflater.inflate(DEFAULT_VIEW_STATE_ERROR_LAYOUT_ID, this, false)
        }
        this.errorView = errorView
        addView(errorView, errorView.layoutParams)

        /**
         * Default view state initialization
         */
        val defaultViewState = typedArray.getInt(R.styleable.MultiStateView_defaultViewState, VIEW_STATE_CONTENT)

        viewState = when (defaultViewState) {
            VIEW_STATE_CONTENT -> State.Content
            VIEW_STATE_ERROR -> State.Error("")
            VIEW_STATE_EMPTY -> State.Empty
            VIEW_STATE_LOADING -> State.Loading
            VIEW_STATE_UNKNOWN -> State.Unknown
            else -> State.Unknown
        }

        /**
         * Empty icon initialization
         */
        val emptyIconRes = typedArray.getResourceId(R.styleable.MultiStateView_emptyIcon, -1)
        if (emptyIconRes > -1) {
            setEmptyIcon(emptyIconRes)
        }

        /**
         * Empty text initialization
         */
        val emptyTextRes = typedArray.getResourceId(R.styleable.MultiStateView_emptyText, -1)
        if (emptyTextRes > -1) {
            setEmptyText(emptyTextRes)
        }

        /**
         * Empty alt text initialization
         */
        val emptyAltTextRes = typedArray.getResourceId(R.styleable.MultiStateView_emptyAltText, -1)
        if (emptyAltTextRes > -1) {
            setEmptyAltText(emptyAltTextRes)
        }

        /**
         * Empty text initialization
         */
        val emptyActionRes = typedArray.getResourceId(R.styleable.MultiStateView_emptyAction, -1)
        if (emptyActionRes > -1) {
            setEmptyAction(emptyActionRes)
        }

        /**
         * Error icon initialization
         */
        val errorIconRes = typedArray.getResourceId(R.styleable.MultiStateView_errorIcon, -1)
        if (errorIconRes > -1) {
            setErrorIcon(errorIconRes)
        }

        /**
         * Error text initialization
         */
        val errorTextRes = typedArray.getResourceId(R.styleable.MultiStateView_errorText, -1)
        if (errorTextRes > -1) {
            setErrorText(errorTextRes)
        }

        /**
         * Error alt text initialization
         */
        val errorAltTextRes = typedArray.getResourceId(R.styleable.MultiStateView_errorAltText, -1)
        if (errorAltTextRes > -1) {
            setErrorAltText(errorAltTextRes)
        }

        /**
         * Error text initialization
         */
        val errorActionRes = typedArray.getResourceId(R.styleable.MultiStateView_errorAction, -1)
        if (errorActionRes > -1) {
            setErrorAction(errorActionRes)
        }


        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (contentView == null) throw IllegalArgumentException("Content view is not defined")
        updateView(viewState)
    }

    /**
     * All of the [addView] methods have been overridden so that it can obtain the content view via XML.
     * It is NOT recommended to add views into [MultiStateView] via the addView methods, but rather use
     * any of the [setViewForState] methods to set views for their given [State] accordingly
     */
    override fun addView(child: View?) {
        if (isValidContentView(child)) contentView = child
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, index)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, index, params)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, params)
    }

    override fun addView(child: View, width: Int, height: Int) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, width, height)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean {
        if (isValidContentView(child)) contentView = child
        return super.addViewInLayout(child, index, params)
    }

    override fun addViewInLayout(
        child: View,
        index: Int,
        params: ViewGroup.LayoutParams,
        preventRequestLayout: Boolean
    ): Boolean {
        if (isValidContentView(child)) contentView = child
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    /**
     * Returns the error view, in order to handle actions
     *
     * For example, reload/refresh the content
     *
     * @return [errorView]
     */
    fun getErrorView(): View? = errorView

    /**
     * Shows the [View] based on the [State]
     */
    private fun updateView(@State.MultiStateViewState viewState: State) {
        when (viewState) {
            is State.Loading -> {
                contentView?.gone()
                errorView?.gone()
                emptyView?.gone()

                loadingView?.visible()
            }

            is State.Empty -> {
                loadingView?.gone()
                errorView?.gone()
                contentView?.gone()
                emptyView?.visible()
            }

            is State.Error -> {
                loadingView?.gone()
                contentView?.gone()
                emptyView?.gone()

                errorView?.visible()

                if (viewState.message.isNotEmpty())
                    setErrorText(viewState.message)
            }
            else -> {
                loadingView?.gone()
                errorView?.gone()
                emptyView?.gone()

                contentView?.visible()
            }
        }
    }

    /**
     * Checks if the given [View] is valid for the [contentView]
     *
     * @param view The [View] to check
     */
    private fun isValidContentView(view: View?): Boolean {
        if (contentView != null && contentView != view) {
            return false
        }

        return view != loadingView && view != errorView && view != emptyView
    }

    /**
     * Sets the view for the given view state
     *
     * @param view          The [View] to use
     * @param state         The [State] to set
     * @param switchToState If the [State] should be switched to by [viewState]
     */
    fun setViewForState(view: View, @State.MultiStateViewState state: State, switchToState: Boolean = false) {
        when (@State.MultiStateViewState state) {
            is State.Loading -> {
                if (loadingView != null) removeView(loadingView)
                loadingView = view
                addView(loadingView)
            }
            is State.Empty -> {
                if (emptyView != null) removeView(emptyView)
                emptyView = view
                addView(emptyView)
            }
            is State.Error -> {
                if (errorView != null) removeView(errorView)
                errorView = view
                addView(errorView)
            }
            is State.Content -> {
                if (contentView != null) removeView(contentView)
                contentView = view
                addView(contentView)
            }
        }

        updateView(viewState)

        if (switchToState) setViewState(state)
    }

    fun setViewForState(@LayoutRes layoutRes: Int, @State.MultiStateViewState state: State, switchToState: Boolean) {
        val view = layoutInflater.inflate(layoutRes, this, false)
        setViewForState(view, state, switchToState)
    }

    fun setViewForState(@LayoutRes layoutRes: Int, @State.MultiStateViewState state: State) {
        setViewForState(layoutRes, state, false)
    }

    fun setViewForState(view: View, @State.MultiStateViewState state: State) {
        setViewForState(view, state, false)
    }

    /**
     * Sets empty icon
     *
     * @param iconRes Drawable resource used as empty icon
     */
    fun setEmptyIcon(@DrawableRes iconRes: Int) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.setCompoundDrawablesWithIntrinsicBounds(0, iconRes, 0, 0)
    }

    /**
     * Sets empty text
     *
     * @param textRes String resource used as empty text
     */
    fun setEmptyText(@StringRes textRes: Int) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.setText(textRes)
    }

    fun setEmptyText(text: String) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.text = text
    }

    fun setEmptyText(text: CharSequence, type: TextView.BufferType) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.setText(text, type)
    }

    fun setEmptyText(text: Spannable) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.text = text
    }

    fun setEmptyAltText(@StringRes textRes: Int) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateAltText)
        textView?.setText(textRes)
        textView?.visibility = View.VISIBLE
    }

    fun setEmptyAltText(text: String) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateAltText)
        textView?.text = text
        textView?.visibility = View.VISIBLE
    }

    fun setEmptyAltText(text: CharSequence, type: TextView.BufferType) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateAltText)
        textView?.setText(text, type)
        textView?.visibility = View.VISIBLE
    }

    fun setEmptyAltText(text: Spannable) {
        val textView = emptyView?.findViewById<TextView>(R.id.emptyStateAltText)
        textView?.text = text
        textView?.visibility = View.VISIBLE
    }

    fun setEmptyAction(@StringRes textRes: Int) {
        val actionButton = emptyView?.findViewById<Button>(R.id.emptyStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.setText(textRes)
        actionButton?.setOnClickListener { emptyStateActionListener?.onEmptyActionEvent() }
    }

    fun setEmptyAction(text: String) {
        val actionButton = emptyView?.findViewById<Button>(R.id.emptyStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.text = text
        actionButton?.setOnClickListener { emptyStateActionListener?.onEmptyActionEvent() }
    }

    fun setEmptyAction(text: CharSequence, type: TextView.BufferType) {
        val actionButton = emptyView?.findViewById<Button>(R.id.emptyStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.setText(text, type)
        actionButton?.setOnClickListener { emptyStateActionListener?.onEmptyActionEvent() }
    }

    /**
     * Sets error icon
     *
     * @param iconRes Drawable resource used as error icon
     */
    fun setErrorIcon(@DrawableRes iconRes: Int) {
        val textView = errorView?.findViewById<TextView>(R.id.emptyStateText)
        textView?.setCompoundDrawablesWithIntrinsicBounds(0, iconRes, 0, 0)
    }

    /**
     * Sets error text
     *
     * @param textRes String resource used as error text
     */
    fun setErrorText(@StringRes textRes: Int) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateText)
        textView?.setText(textRes)
    }

    fun setErrorText(message: String) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateText)
        textView?.text = message
    }

    fun setErrorText(message: CharSequence, type: TextView.BufferType) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateText)
        textView?.setText(message, type)
    }

    fun setErrorText(message: Spannable) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateText)
        textView?.text = message
    }

    fun setErrorAltText(@StringRes textRes: Int) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateAltText)
        textView?.setText(textRes)
        textView?.visibility = View.VISIBLE
    }

    fun setErrorAltText(message: String) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateAltText)
        textView?.text = message
        textView?.visibility = View.VISIBLE
    }

    fun setErrorAltText(message: CharSequence, type: TextView.BufferType) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateAltText)
        textView?.setText(message, type)
        textView?.visibility = View.VISIBLE
    }

    fun setErrorAltText(message: Spannable) {
        val textView = errorView?.findViewById<TextView>(R.id.errorStateAltText)
        textView?.text = message
        textView?.visibility = View.VISIBLE
    }

    fun setErrorAction(@StringRes textRes: Int) {
        val actionButton = errorView?.findViewById<Button>(R.id.errorStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.setText(textRes)
        actionButton?.setOnClickListener { errorStateActionListener?.onErrorActionEvent() }
    }

    fun setErrorAction(message: String) {
        val actionButton = errorView?.findViewById<Button>(R.id.errorStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.text = message
        actionButton?.setOnClickListener { errorStateActionListener?.onErrorActionEvent() }
    }

    fun setErrorAction(message: String, type: TextView.BufferType) {
        val actionButton = errorView?.findViewById<Button>(R.id.errorStateActionButton)
        actionButton?.visibility = View.VISIBLE
        actionButton?.setText(message, type)
        actionButton?.setOnClickListener { errorStateActionListener?.onErrorActionEvent() }
    }

    /**
     * Sets the [StateListener] for the view
     *
     * @param listener The [StateListener] that will receive callbacks
     */
    fun setStateListener(listener: StateListener) {
        this.listener = listener
    }

    /**
     * Shows [errorView] as a main view
     */
    fun showErrorView() {
        showErrorView("")
    }

    fun showErrorView(message: String) {
        setViewState(State.Error(message))
    }


    /**
     * Shows [emptyView] as a main view
     */
    fun showEmptyView() {
        setViewState(State.Empty)
    }

    /**
     * Shows [contentView] as a main view
     */
    fun showContent() {
        setViewState(State.Content)
    }

    /**
     * Shows [loadingView] as a main view
     */
    fun showLoading() {
        setViewState(State.Loading)
    }

    /**
     * Internal method for switching between available views
     *
     * @param state The [State]
     */
    private fun setViewState(@State.MultiStateViewState state: State) {
        viewState = state
        updateView(viewState)
        listener?.onStateChanged(state)
    }

    fun setEmptyStateActionListener(listener: EmptyStateActionListener) {
        emptyStateActionListener = listener
    }

    fun setEmptyStateActionListener(listener: () -> Unit) {
        emptyStateActionListener = object : EmptyStateActionListener {
            override fun onEmptyActionEvent() {
                listener()
            }
        }
    }

    fun setErrorStateActionListener(listener: ErrorStateActionListener) {
        errorStateActionListener = listener
    }

    fun setErrorStateActionListener(listener: () -> Unit) {
        errorStateActionListener = object : ErrorStateActionListener {
            override fun onErrorActionEvent() {
                listener()
            }
        }
    }

    @State.MultiStateViewState
    fun getViewState(): State = viewState

    interface StateListener {
        /**
         * Callback for when the [viewState] has changed
         *
         * @param state The [State] that was switched to
         */
        fun onStateChanged(@State.MultiStateViewState state: State)
    }


    interface EmptyStateActionListener {
        fun onEmptyActionEvent()
    }

    interface ErrorStateActionListener {
        fun onErrorActionEvent()
    }

}