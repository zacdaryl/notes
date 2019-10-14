# React Native

## Log

> react-native log-ios        # For iOS
> 
> react-native log-android    # For Android

## Functional vs Class Components

https://guide.freecodecamp.org/react-native/functional-vs-class-components/

```
const PageOne = () => {
    return (
        <h1>Page One</h1>
    );
}
```

```
class App extends Component {
    render () {
        return (
            <Text>Hello World!</Text>
        )
    }
}
```


Class components are used as container components to handle state management and wrap child components. Functional components generally are just used for display purposes - these components call functions from parent components to handle user interactions or state updates.

