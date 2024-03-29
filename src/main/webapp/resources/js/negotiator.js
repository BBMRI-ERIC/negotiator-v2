
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
                //if( log ) alert(log);
            }

        });
    });
}

/**
 * Sets up the panel to be links to the query details.
 */
function setupQueryLinks() {
    $(".clickable").click(function() {
        window.location.href = $(this).find("a.detailLink").attr('href');
    });

    $(".clickable a").click(function(e) {
        e.stopPropagation();
    });
}

/**
 * Initializes the truncate-panel elements with the jquery truncate plugin
 */
$(function() {
    $(".truncate-panel").truncate({
        multiline: true
    });
});

function showBiobankerHelp() {
    document.getElementById('biobankers-crashcourse').style.display = 'block';
    document.getElementById('researchers-crashcourse').style.display = 'none';
}

function showResearcherHelp() {
    document.getElementById('researchers-crashcourse').style.display = 'block';
    document.getElementById('biobankers-crashcourse').style.display = 'none';
}

function commentReadUpdate(commentId, scope) {
    if(scope.startsWith('comment')) {
        publicCommentReadUpdate(commentId);
    } else if(scope.startsWith('private')) {
        privateCommentReadUpdate(commentId);
    }
}

function privateCommentReadUpdate(commentId) {
    // Update comment area
    var updateCommentReadStatus = document.getElementsByClassName("updateCommentReadForComment" + commentId);
    document.getElementsByClassName("updateCommentReadForCommentShowElement" + commentId)[0].style.display = "none";
    document.getElementsByClassName("updateCommentReadForCommentShowMarkAsReadButton" + commentId)[0].style.display = "none";
    // Trigger the ajax call to set comment as read (in every comment block is one of this)
    updateCommentReadStatus[0].click();
    // Update the orange views to match the update
    var tabSection = document.getElementById("third").getElementsByClassName("queryNumberResponsesBadge")[0].innerText;
    var unredcount = tabSection.split('/')[0]-1;
    if(unredcount<=0) {
        document.getElementById("third").getElementsByClassName("queryNumberResponsesBadge")[0].style.backgroundColor = "#000000";
        document.getElementsByClassName("selected-query")[0].getElementsByClassName("queryNumberResponsesBadge")[1].style.backgroundColor = "#000000";
    }
    document.getElementById("third").getElementsByClassName("queryNumberResponsesBadge")[0].innerHTML = '<i class="glyphicon glyphicon-tower"></i>   '+unredcount+'/'+tabSection.split('/')[1];
    // Update overview table left side
    document.getElementsByClassName("selected-query")[0].getElementsByClassName("queryNumberResponsesBadge")[1].innerHTML = '<i class="glyphicon glyphicon-tower"></i>   '+unredcount+'/'+tabSection.split('/')[1];
}

function publicCommentReadUpdate(commentId) {
    // Update comment area
    var updateCommentReadStatus = document.getElementsByClassName("updateCommentReadForComment" + commentId);
    document.getElementsByClassName("updateCommentReadForCommentShowElement" + commentId)[0].style.display = "none";
    document.getElementsByClassName("updateCommentReadForCommentShowMarkAsReadButton" + commentId)[0].style.display = "none";
    // Trigger the ajax call to set comment as read (in every comment block is one of this)
    updateCommentReadStatus[0].click();
    // Update the orange views to match the update
    var tabSection = document.getElementById("second").getElementsByClassName("queryNumberResponsesBadge")[0].innerText;
    var unredcount = tabSection.split('/')[0]-1;
    if(unredcount<=0) {
        document.getElementById("second").getElementsByClassName("queryNumberResponsesBadge")[0].style.backgroundColor = "#000000";
        document.getElementsByClassName("selected-query")[0].getElementsByClassName("queryNumberResponsesBadge")[0].style.backgroundColor = "#000000";
    }
    document.getElementById("second").getElementsByClassName("queryNumberResponsesBadge")[0].innerHTML = '<i class="glyphicon glyphicon-bullhorn"></i>   '+unredcount+'/'+tabSection.split('/')[1];
    // Update overview table left side
    document.getElementsByClassName("selected-query")[0].getElementsByClassName("queryNumberResponsesBadge")[0].innerHTML = '<i class="glyphicon glyphicon-bullhorn"></i>   '+unredcount+'/'+tabSection.split('/')[1];
}

function publicCommentReadAllReadUpdate() {
    var elements = document.querySelectorAll('[class^="updateCommentReadForComment"]');
    for(var i = 0; i < elements.length; i++)
    {
        var elementUpdateId = elements[i].className.replaceAll('updateCommentReadForComment', '');
        if(document.getElementsByClassName("updateCommentReadForCommentShowElement" + elementUpdateId)[0].style.display == "none") {
            //console.log(elementUpdateId)
        } else {
            publicCommentReadUpdate(elementUpdateId);
        }
    }
}
