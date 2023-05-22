import React from "react";
import axios from "axios";
import { FixedSizeGrid as List } from "react-window";



export default class MainBooks extends React.Component {
constructor(props) {
    super(props);
    this.state = {value:'', search:'', books:''};
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
      this.setState({value: event.target.value});
  }

  handleSubmit(event) {
    axios.get("https://www.googleapis.com/books/v1/volumes?q="+this.state.value+"&maxResults=25").then((response) => {this.setState({search : response.data.items}); 
    this.setState({books:response.data.items})});
    event.preventDefault();
  }

  render() {
    const books = this.state.books;
    return (
        <div>

{/* <div class=" flex justify-center items-center h-screen"> */}


<div >
 <form onSubmit={this.handleSubmit}>   
    <label htmlFor="default-search" class="mb-2 text-sm font-medium text-gray-900 sr-only dark:text-white">Search</label>
    <div class="relative">
        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
            <svg aria-hidden="true" class="w-5 h-5 text-gray-500 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
        </div>
        <input type="search" value={this.state.value} onChange={this.handleChange} id="default-search" class="block w-full p-4 pl-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" 
        placeholder="Chercher des livres" required></input>
        <button type="submit" value="Submit" class="text-white absolute right-2.5 bottom-2.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">Search</button>
    </div>
 </form>
 <hr></hr>


<p>Advanced search</p>
</div>


<div class="" style={{height: "540px", overflow: "scroll", overflowX: "hidden" }}>
{books.length > 0 && books.map((book, index) => (
              <div key={index}>
              <br></br>
                {/* <div>Author: {book.volumeInfo.authors}</div> */}
                {/* <div>Published Date: {book.volumeInfo.publishedDate}</div>
                <div>Description: {book.volumeInfo.description}</div>
                <img src={book.volumeInfo.imageLinks.thumbnail} alt="Book cover"></img> */}
                {/* <div class="min-h-screen bg-gray-50 flex flex-col justify-center relative overflow-hidden sm:py-12"> */}
<div class="max-w-7xl mx-auto">
    <div class="relative group">
      <div class="absolute -inset-1 bg-gradient-to-r s rounded-lg blur opacity-25 group-hover:opacity-100 transition duration-1000 group-hover:duration-200"></div>
      <div class="ml-12 mr-6 shadow-xl relative px-7 py-5  ring-1 ring-gray-900/5 rounded-lg leading-none  items-top justify-start ">
        {/* <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24">
          <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M6.75 6.75C6.75 5.64543 7.64543 4.75 8.75 4.75H15.25C16.3546 4.75 17.25 5.64543 17.25 6.75V19.25L12 14.75L6.75 19.25V6.75Z"></path>
        </svg> */}
        <div class="">
        <img src="" alt="Book cover"></img>
          <p class="text-slate-800">Titre : {book.volumeInfo.title}</p>
<span class="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-green-600 bg-green-200 uppercase last:mr-0 mr-1">
{book.volumeInfo.publishedDate}
</span>
<span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-slate-600 bg-slate-200 uppercase last:mr-0 mr-1">
ISBN : 
</span>
<div>Author: {book.volumeInfo.authors}</div>
          <a href="https://braydoncoyer.dev/blog/tailwind-gradients-how-to-make-a-glowing-gradient-background" class="block text-indigo-400 group-hover:text-slate-800 transition duration-200" target="_blank">Read Article →</a>
        </div>
      </div>
    </div>
  </div>
  
</div>

))}
</div>



      </div>
      
    );
  }
}