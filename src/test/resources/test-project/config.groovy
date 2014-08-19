testProperty = "Test Property Value"

homePage = "home.md"

filter = {
    before { text ->
        text.replaceAll(/MY_REPLACE_TEXT_1/, '**HELLO**')
    }
    after { text ->
        text.replaceAll(/<h2.*?>MY_REPLACE_TEXT_2<\/h2>/, '<i>BYE</i>')
    }
    afterTemplate { text ->
        text.replaceAll(/TITLE/, 'GOOD')
    }
}
