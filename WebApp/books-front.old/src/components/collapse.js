import React from "react";

export default class myCollapse extends React.Component {
    constructor(props) {
      super(props);
      this.state = { isOpen: false };
      this.handleClick = this.handleClick.bind(this);
    }
  
    handleClick() {
      this.setState(state => ({ isOpen: !state.isOpen }));
    }
  
    render() {
      return (
        <div>
          <button onClick={this.handleClick}>
            {this.state.isOpen ? 'Close' : 'Open'}
          </button>
          {this.state.isOpen && <div>{this.props.children}</div>}
        </div>
      );
    }
  }
  