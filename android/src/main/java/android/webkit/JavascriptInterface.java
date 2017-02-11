package android.webkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that allows exposing methods to JavaScript. Starting from API level
 * {@link android.os.Build.VERSION_CODES#} and above, only methods explicitly
 * marked with this annotation are available to the Javascript code. See
 * {@link android.webkit.WebView#addJavascriptInterface} for more information about it.
 */
@SuppressWarnings("javadoc")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JavascriptInterface {
}