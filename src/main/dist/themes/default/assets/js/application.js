/*
 * Copyright 2013-2014 the original author or authors
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

$(function () {
    // Sidebar
    (function () {
        var $content = $('.content');
        var $sidebar = $('.sidebar');
        var $sidebarToggle = $('.sidebar-toggle');
        var $footer = $('.footer');
        var $navLink = $('.content-nav a');

        var hideSidebar = function () {
            $sidebar.hide();
            $content.removeClass('content-sidebar-visible');
        }
        var showSidebar = function () {
            if (location.search.indexOf('sidebar=no') >= 0) {
                location.href = location.href.replace(/\?sidebar=no/, '');
                return false;
            }
            $sidebar.show();
            $content.addClass('content-sidebar-visible');
        }
        var goUrlWithNoSidebarParam = function (anchor) {
            location.href = anchor.href.replace(anchor.hash, '') + "?sidebar=no" + anchor.hash;
        }

        // Toggle a sidebar
        $sidebarToggle.on('click', function (e) {
            if ($sidebar.is(':visible')) {
                hideSidebar();
            } else {
                showSidebar();
            }
        });

        // Keep a status of a sidebar after clicking a navLink
        $navLink.on('click', function (e) {
            if (!$sidebar.is(':visible')) {
                e.preventDefault();
                goUrlWithNoSidebarParam(this);
            }
        });

        // Hide a sidemenu if a small screen mode.
        $sidebar.find("a").on("click", function (e) {
            if (window.matchMedia('(max-width: 768px)').matches) {
                e.preventDefault();
                goUrlWithNoSidebarParam(this);
            }
        });

        // Initialize
        if (location.search.indexOf('sidebar=no') >= 0) {
            hideSidebar();
        } else {
            showSidebar();
        }
    })();

    // Pretty print of code
    (function () {
        $('pre').addClass('prettyprint');
        prettyPrint();
    })();

    // Responsive support for <table>
    (function () {
        $('table').wrap('<div class="table-responsive"></div>');
    })();

    // Fix a scroll offset for a link within a page
    (function () {
        var activateLinkOfSidebar = function () {
            // Refresh an active link
            $(".sidebar a.active").removeClass("active");
            $(".sidebar a").each(function () {
                var altHash = $(this).data("alt-hash");
                if (this.href === location.href          // perfect match
                        || this.href + '#' === location.href // the actual href has a just empty hash '#'
                        || (altHash && this.href.replace(/#.*$/, '') + "#" + altHash === location.href)) { // an alternative hash for a first head of a page
                    $(this).closest(".visible").children("a").addClass("active");
                }
            });

            // Fixing offset
            if ($(".sidebar a.active").length) {
                $(".sidebar").scrollTop($(".sidebar .active").position().top - $(".sidebar li:first").position().top + 10); // 10 means an offset to adjust a position
            }
        }

        $(document).on("click", "a", function (e) {
            // In the case of going to another page
            if (location.pathname.replace(/^\//, '') !== this.pathname.replace(/^\//, '') || location.hostname !== this.hostname) {
                return true;
            }
            // Since here, for scrolling within a current page

            // Find a target link
            var $target = null;
            if (this.hash) {
                var $targetById = $(this.hash);
                var $targetByAnchor = $('a[name="' + this.hash.slice(1) + '"]');
                $target = $targetById.length ? $targetById : ($targetByAnchor.length ? $targetByAnchor : false);
                if (!$target) {
                    return false;
                }
            }

            // Set a hash for an address bar (this operation causes scrolling)
            location.hash = this.hash;
            activateLinkOfSidebar();

            if ($target) {
                // Fixing offset
                // The duration of animate() requires more than 1.
                // If not, it cannot be work in case of a direct access.
                var headerOffset = $(".header").height() + 5;
                $('html, body').animate({ scrollTop: $target.offset().top - headerOffset }, 1);

                // Blinking the target element
                $target.fadeTo('fast', 0.5).fadeTo('slow', 1.0).fadeTo('fast', 0.5).fadeTo('slow', 1.0);
            }
            return false;
        });

        // For a direct access
        if (location.hash && location.hash !== '#') {
            $('a[href="' + location.hash + '"]').click();
        }

        // Initialize
        activateLinkOfSidebar();
    })();
});

