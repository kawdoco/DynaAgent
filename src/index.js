import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';

const theme = createMuiTheme({
    typography:{
        fontFamily: 'Raleway'
    },
    palette: {
        type: 'dark',
        primary: {
            light: '#16a085',
            main: '#171717',
            dark: '#0d5b4c',
            contrastText: '#fff',
          },
        secondary: {
            light: "#B33771",
            main: "#FD7272",
            dark : "#82589F",
            contrastText: "#fff"
        },
      },
      status: {
        danger: 'orange',
      }
});

function Root() {
    return (
      <MuiThemeProvider theme={theme}>
        <App />
      </MuiThemeProvider>
    );
  }

  
ReactDOM.render(<Root />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
