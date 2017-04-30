(function($){
  $(function(){

    $('.button-collapse').sideNav();
    $(".dropdown-button").dropdown();
    $('.modal').modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });

  });
})(jQuery);
