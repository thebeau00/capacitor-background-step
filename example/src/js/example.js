import { Backgroundstep } from 'capacitor-background-step';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    Backgroundstep.echo({ value: inputValue })
}
