import React from "react";
import axios from "axios";
import { Collapse } from "react-collapse";
import Scrollbars from "react-custom-scrollbars";
import { FontawesomeObject } from "@fortawesome/fontawesome-svg-core";

export default class Index extends React.Component {
    constructor(){
        super();
        this.state = {value:'', search:'', books:'', 
        isOpen: false,  collapsed: [], 
        advancedCollpase:'',

      };


        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }
  
    handleSubmit(event) {
      axios.get("https://www.googleapis.com/books/v1/volumes?q="+this.state.value+"&maxResults=25").then((response) => {this.setState({search : response.data.items}); 
      this.setState({books:response.data.items})});
      console.log("done")
      event.preventDefault();
    }

    handleCollapse(index) {
        const collapsed = this.state.collapsed.slice();
        collapsed[index] = !collapsed[index];
        this.setState({ collapsed });
    }


    advancedColl = () => {
      this.setState({advancedCollapse: 'true'})
      console.log(this.state.advancedCollpase)
    }

render(){
    const books = this.state.books;
    const book_zero = 'industryIdentifiers[0]';
    return(

<div class="min-h-min bg-transparent flex flex-col justify-center ">
  <div class="relative p-12 w-full sm:max-w-2xl sm:mx-auto ">
    <div class="overflow-hidden z-0 rounded-full relative p-3 shadow-lg">
        <form role="form" class="relative flex z-50 bg-white rounded-full" onSubmit={this.handleSubmit}>
        <input type="text" placeholder="chercher des livres" class="rounded-full flex-1 px-6 py-4 text-gray-700 focus:outline-none font-sans" value={this.state.value} onChange={this.handleChange}></input>
        <button class="bg-indigo-500 text-white rounded-full font-semibold px-8 py-4 hover:bg-indigo-400 focus:bg-indigo-600 focus:outline-none">Search</button>
        </form>
        <div class="glow glow-1 z-10 bg-pink-400 absolute"></div>
        <div class="glow glow-2 z-20 bg-purple-400 absolute"></div>
        <div class="glow glow-3 z-30 bg-yellow-400 absolute"></div>
        <div class="glow glow-4 z-40 bg-blue-400 absolute"></div>
    </div>

   
</div>




<div className="grid h-screen place-items-center" style={{height: "540px", overflow: "scroll", overflowX: "hidden" }}>
{books.length > 0 && books.map((book, index) => ( 
<div key={index}>
              <br></br>
     <div className="bg-white rounded-md shadow-2xl w-[600px] ">
       <div className="p-4">
      <h3 className="text-lg font-serif">{book.volumeInfo.title} : </h3>
        <hr></hr>
<span class="mt-4 mb-4 text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-green-600 bg-green-200 uppercase last:mr-0 mr-1">
{book.volumeInfo.publishedDate}
</span>
<span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-yellow-600 bg-yellow-200 uppercase last:mr-0 mr-1">
{book.volumeInfo.authors}
</span>
<span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-gray-600 bg-gray-200 uppercase last:mr-0 mr-1">
{book.volumeInfo.pageCount} pages.
</span>

<span class="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-blue-600 bg-blue-200 uppercase last:mr-0 mr-1">
Lang : {book.volumeInfo.language}
</span>


   <br></br>
   <button className="bg-white hover:bg-gray-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow" 
   onClick={() => this.handleCollapse(index)}>Description</button>
                    <Collapse isOpened={this.state.collapsed[index]}>
                        <Scrollbars style={{ height: 200, width:"fill" }}>
                          <br></br>
                        <p className="font-mono">{book.volumeInfo.description}</p>
                            </Scrollbars>
                    </Collapse>
  <br></br>
<button class="bg-transparent hover:bg-blue-500 text-blue-700 font-semibold hover:text-white py-2 px-4 border border-blue-500 hover:border-transparent rounded">
<i class="fa-solid fa-bookmark"></i> Button
</button>
      </div>
    </div>
</div>
))}
</div> 
{/* Overflow end! */}
</div>
);}}