$(document).ready(function(){
    $(".affiliation-collapsible-plus").click(function( event ) {
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-content" ).css('display', 'block');
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-plus" ).css('display', 'none');
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-minus" ).css('display', 'inline');
    });
});

$(document).ready(function(){
    $(".affiliation-collapsible-minus").click(function( event ) {
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-content" ).css('display', 'none');
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-plus" ).css('display', 'inline');
        $(event.delegateTarget).parent().find( ".affiliation-collapsible-minus" ).css('display', 'none');
    });
});