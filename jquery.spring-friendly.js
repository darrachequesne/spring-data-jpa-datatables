// From https://github.com/jquery/jquery/blob/master/src/serialize.js
// Overrides data serialization to allow Spring MVC to correctly map input parameters : column[0][data] now becomes column[0].data
(function($) {
	var r20 = /%20/g, rbracket = /\[\]$/, rCRLF = /\r?\n/g, rsubmitterTypes = /^(?:submit|button|image|reset|file)$/i, rsubmittable = /^(?:input|select|textarea|keygen)/i;

	function customBuildParams(prefix, obj, traditional, add) {
		var name;

		if (jQuery.isArray(obj)) {
			// Serialize array item.
			jQuery.each(obj, function(i, v) {
				if (traditional || rbracket.test(prefix)) {
					// Treat each array item as a scalar.
					add(prefix, v);

				} else {
					// Item is non-scalar (array or object), encode its numeric
					// index.
					customBuildParams(prefix + "["
							+ (typeof v === "object" ? i : "") + "]", v,
							traditional, add);
				}
			});

		} else if (!traditional && jQuery.type(obj) === "object") {
			// Serialize object item.
			for (name in obj) {
				// This is where the magic happens
				customBuildParams(prefix + "." + name, obj[name], traditional,
						add);
			}

		} else {
			// Serialize scalar item.
			add(prefix, obj);
		}
	}

	$.param = function(a, traditional) {
		var prefix, s = [], add = function(key, value) {
			// If value is a function, invoke it and return its value
			value = jQuery.isFunction(value) ? value() : (value == null ? ""
					: value);
			s[s.length] = encodeURIComponent(key) + "="
					+ encodeURIComponent(value);
		};

		// Set traditional to true for jQuery <= 1.3.2 behavior.
		if (traditional === undefined) {
			traditional = jQuery.ajaxSettings
					&& jQuery.ajaxSettings.traditional;
		}

		// If an array was passed in, assume that it is an array of form
		// elements.
		if (jQuery.isArray(a) || (a.jquery && !jQuery.isPlainObject(a))) {
			// Serialize the form elements
			jQuery.each(a, function() {
				add(this.name, this.value);
			});

		} else {
			// If traditional, encode the "old" way (the way 1.3.2 or older
			// did it), otherwise encode params recursively.
			for (prefix in a) {
				customBuildParams(prefix, a[prefix], traditional, add);
			}
		}

		// Return the resulting serialization
		return s.join("&").replace(r20, "+");
	};
})(jQuery);