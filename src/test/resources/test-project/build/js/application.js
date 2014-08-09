/*
 * Copyright 2013 the original author or authors
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
    var $content = $('.content');
    var $toc = $('.toc');
    var $tocToggle = $('.toc-toggle');
    var $footer = $('.footer');
    var $navLink = $('.content-nav a');

    function hideToc() {
        $toc.hide();
        $content.removeClass('content-toc-visible');
        $footer.removeClass('footer-toc-visible');
    }

    function showToc() {
        $toc.show();
        $content.addClass('content-toc-visible');
        $footer.addClass('footer-toc-visible');
    }

    // bind events

    $tocToggle.on('click', function () {
        if ($toc.is(':visible')) {
            hideToc();
        } else {
            showToc();
        }
    });
    $navLink.on('click', function (e) {
        if (!$toc.is(':visible')) {
            e.preventDefault();
            location.href = $(this).attr('href') + "?toc=hidden";
        }
    });

    // initialize

    if (location.search.indexOf('toc=hidden') !== -1) {
        hideToc();
    }

    $('pre').addClass('prettyprint');
    prettyPrint();

    $('table').wrap('<div class="table-responsive"></div>');
});

