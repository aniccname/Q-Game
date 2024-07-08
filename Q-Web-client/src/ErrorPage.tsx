//JSX file because I really do not want to deal with types in a try catch statement. 
import { Container, Stack } from "@mui/material"
import React from "react"

/**
 * Displays the formatted error message
 */
export default function ErrorMessage({msg} : {msg : string}) {
    return (
        <Container maxWidth="md">
            <Stack direction="column" spacing={2}>
                <p className="error-help-message"> The following error has occured: </p>
                <p className="error-message">"<i>{msg}</i>"</p>
                <p className="error-help-message">
                    Your connection to the server has been terminated. Reload the page to try again.
                </p>
            </Stack>
        </Container>
        )
}
