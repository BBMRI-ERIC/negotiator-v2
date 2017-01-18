
$(function() {
    jsf.ajax.addOnEvent(function (data) {
        if (data.status == "success") {
            var viewState = getViewState(data.responseXML);

            for (var i = 0; i < document.forms.length; i++) {
                var form = document.forms[i];

                if (form.method == "post" && !hasViewState(form)) {
                    createViewState(form, viewState);
                }
            }
        }
    });
});

function getViewState(responseXML) {
    var updates = responseXML.getElementsByTagName("update");

    for (var i = 0; i < updates.length; i++) {
        if (updates[i].getAttribute("id").match(/^([\w]+:)?javax\.faces\.ViewState(:[0-9]+)?$/)) {
            return updates[i].textContent || updates[i].innerText;
        }
    }

    return null;
}

function hasViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].name == "javax.faces.ViewState") {
            return true;
        }
    }

    return false;
}

function createViewState(form, viewState) {
    var hidden;

    try {
        hidden = document.createElement("<input name='javax.faces.ViewState'>"); // IE6-8.
    } catch(e) {
        hidden = document.createElement("input");
        hidden.setAttribute("name", "javax.faces.ViewState");
    }

    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("value", viewState);
    hidden.setAttribute("autocomplete", "off");
    form.appendChild(hidden);
}

/**
 * initializes the file upload input to make it a real bootstrap form.
 */
function initializeFileUpload() {
    // We can attach the `fileselect` event to all file inputs on the page
    $(document).on('change', ':file', function() {
        var input = $(this),
            numFiles = input.get(0).files ? input.get(0).files.length : 1,
            label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
        input.trigger('fileselect', [numFiles, label]);
    });

    // We can watch for our custom `fileselect` event like this
    $(document).ready( function() {
        $(':file').on('fileselect', function(event, numFiles, label) {

            var input = $(this).parents('.input-group').find(':text'),
                log = numFiles > 1 ? numFiles + ' files selected' : label;

            if( input.length ) {
                input.val(log);
            } else {
                if( log ) alert(log);
            }

        });
    });
}

/**
 * Sets up the panel to be links to the query details.
 */
function setupQueryLinks() {
    $(".panel").click(function() {
        window.location.href = $(this).find("a.detailLink").attr('href');
    });

    $(".panel a").click(function(e) {
        e.stopPropagation();
    });
}