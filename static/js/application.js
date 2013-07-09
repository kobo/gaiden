// NOTICE!! DO NOT USE ANY OF THIS JAVASCRIPT
// IT'S ALL JUST JUNK FOR OUR DOCS!
// ++++++++++++++++++++++++++++++++++++++++++

function getScrollElement() {
	var $window = $(window),
		wst = $window.scrollTop();
	$window.scrollTop( wst + 1 );
	if ( $('html').scrollTop() > 0 ) {
		return 'html';
	} else if ( $('body').scrollTop() > 0 ) {
		return 'body';
	}
}


$('.bs-doc-sidenav').affix({
	offset: {
		top: 300,
		bottom: 210
	}
}).find('a').click(function(e) {
	var $scrollElement = $(getScrollElement()),
		y = $($(this).attr('href')).offset().top;
	$scrollElement.stop().animate({scrollTop: y});
	return false;
});

$('pre').addClass('prettyprint linenums');

prettyPrint();
