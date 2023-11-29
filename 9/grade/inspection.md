Pair: plucky-monkeys \
Commit: [51095d98f64051fcab9c137126a885f07e4a66c9](https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/tree/51095d98f64051fcab9c137126a885f07e4a66c9) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/c73d4238d56202b13f3f0ae631091bed7b6a8b85/9/self-9.md \
Score: 175/210 \
Grader: Can Ivit

## Self Eval [20/20]
Thank you for honest and helpful self eval

## Programming [155/190]
- If the `ProxyPlayer` takes in `InputStream` and `OutputStream` or some kind of higher level `JsonReader` and `JsonWriter` instead of a raw TCP `Socket`, writing unit tests with mock sources would be easier. Also, raw TCP socket gives more power to the ProxyPlayer than it needs, such as the ability to close the connection.
- [-5] `ProxyReferee` should not ignore the exception in the `playGame` loop.
- Same comment applies to `ProxyReferee` about taking in raw TCP socket.
- [-20] The client should not crash if the server at the specified host and port is unavailable.
- [-10] The server's two waiting periods (20 second each) is hard-coded by calling `acceptSignups` twice. This should be abstracted such that any number of waiting periods can be applied. 
