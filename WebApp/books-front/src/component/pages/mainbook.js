import React from "react";
import axios from "axios";
import { Collapse } from "react-collapse";
import Scrollbars from "react-custom-scrollbars";
import { AwesomeButton } from "react-awesome-button";
import AwesomeButtonProgress from "react-awesome-button";
import 'react-awesome-button/dist/styles.css';
import { NumberPicker } from "react-widgets";
import "react-widgets/styles.css";
import cogoToast from "cogo-toast";
import { Link } from "react-router-dom";



export default class MainBooks extends React.Component {
    constructor(){
        super();
        this.state = 
        {
        value:'', search:'', books:'', 
        isOpen: false,  collapsed: [], 
        advancedCollpase:'', 
        isOpenadvanced: false,
        resultsNum: 25,
        selectedOption: 'newest',
      };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChangenum = this.handleChangenum.bind(this);
        this.handleChangeselect = this.handleChangeselect.bind(this);
        this.passValues = this.passValues.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleChangenum(newValue) {
      this.setState({resultsNum: newValue });
      // console.log(this.state.resultsNum)
    }
  
    handleSubmit(event) {
      axios.get("https://www.googleapis.com/books/v1/volumes?q="+this.state.value+"&maxResults="+this.state.resultsNum+"&orderBy="+this.state.selectedOption).then((response) => {this.setState({search : response.data.items}); 
      this.setState({books:response.data.items})});
      event.preventDefault();
    }

    handleCollapse(index) {
        const collapsed = this.state.collapsed.slice();
        collapsed[index] = !collapsed[index];
        this.setState({ collapsed });
    }

    toggleCollapse = () => {
      this.setState(prevState => ({isOpenadvanced: !prevState.isOpenadvanced,}));
    }

    handleChangeselect = (event) => {
        this.setState({ selectedOption: event.target.value });
        // console.log(this.state.selectedOption)
    };

passValues = (book) => {
//  console.log(book)
 const alertBook_name = book[0];
 const booksave_string = JSON.stringify(book)
//  console.log(book[0], book[1], book[2], book[3])
//  console.log(booksave_string)
// 
 const instance = axios.create({
  baseURL: 'http://127.0.0.1:8000',
  headers: {'Access-Control-Allow-Origin': '*',},
});

instance.post("/api/v1/save?book_name="+book[0]+"&language="+book[1]+"&release_date="+book[2]+"&page_count="+book[3]).then((response) => {
    cogoToast.success("Votre livre "+'"'+alertBook_name+'"'+" a été sauvegardé", { position: 'top-right', heading: 'Succès' });})
  .catch((error) => {
    cogoToast.warn("Une erreur est survenue.", { position: 'top-right', heading: 'Erreur!' });
  });      
}

render(){
    const books = this.state.books;
    const book_zero = 'industryIdentifiers[0]';
    const select_options = [
            { value: 'newest', label: 'Le plus récent' },
            { value: 'relevance', label: 'Pertinence' },
            { value: 'ORDER_BY_INDEFINED', label: 'Indéterminée' },];

    return(
      
 <div>
      <nav class="font-sans flex flex-col text-center sm:flex-row sm:text-left sm:justify-between py-4 px-6 bg-white shadow sm:items-baseline w-full">
        <div class="mb-2 sm:mb-0">
        </div>
        <div>
        <AwesomeButton type="secondary" href="/saved">Mes favoris</AwesomeButton>
        </div>
      </nav>



<div class="min-h-min bg-transparent flex flex-col justify-center ">
  <div class="hover:backdrop-blur-sm relative p-12 w-full sm:max-w-2xl sm:mx-auto my-px ">
    <div class="overflow-hidden z-0 rounded-full relative p-3 shadow-lg">
        <form role="form" class="relative flex z-50 bg-white rounded-full" onSubmit={this.handleSubmit}>
        <input type="text" placeholder="Chercher des livres par Nom ou ISBN..." class="rounded-full flex-1 px-6 py-4 text-gray-700 focus:outline-none font-sans" value={this.state.value} onChange={this.handleChange}></input>
        <button class="bg-indigo-500 text-white rounded-full font-semibold px-8 py-4 hover:bg-indigo-400 focus:bg-indigo-600 focus:outline-none">Search</button>
        </form>
        <div class="glow glow-1 z-10 bg-pink-400 absolute"></div>
        <div class="glow glow-2 z-20 bg-purple-400 absolute"></div>
        <div class="glow glow-3 z-30 bg-yellow-400 absolute"></div>
        <div class="glow glow-4 z-40 bg-blue-400 absolute"></div>       
    </div>
    {/* <AwesomeButton type="secondary">Anchor</AwesomeButton> */}
    
    
    <div class="mt-[7px] ml-[25px] w-48 flex grid">
      <div class="">
        <AwesomeButton onPress={this.toggleCollapse} type="secondary">Recherche avancée</AwesomeButton>
        <Collapse isOpened={this.state.isOpenadvanced}>
          <br></br>
        <p class="font-serif">Nombre de résultats :</p><NumberPicker max={40} defaultValue={this.state.resultsNum} onChange={this.handleChangenum} />
          <p class="font-serif">Filtrer par :</p><select value={this.state.selectedOption} onChange={this.handleChangeselect} class="form-select appearance-none block w-full px-3 py-1.5 text-base font-normal text-gray-700 bg-white bg-clip-padding bg-no-repeat border border-solid border-gray-300 rounded transition
      ease-in-out m-0 focus:text-gray-700 focus:bg-white focus:border-blue-600 focus:outline-none" >
                {select_options.map(option => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
        </Collapse>
        </div>
    </div>
</div>

<div className="grid h-screen place-items-center pb-[60px]" style={{height: "540px", overflow: "scroll", overflowX: "hidden" }}>
{books.length > 0 && books.map((book, index) => ( 
<div key={index}>
              <br></br>
     <div className="backdrop-blur-sm rounded-md shadow-2xl w-[600px]">
       <div className="p-4">
      <h3 className="text-lg font-serif"><i class="fa-regular fa-books"></i>Livre : {book.volumeInfo.title} : </h3>
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
<AwesomeButton onPress={() => this.handleCollapse(index)}>Détails</AwesomeButton>
                    <Collapse isOpened={this.state.collapsed[index]}>
                         <Scrollbars style={{ height: 200, width:"fill" }}>
                          <br></br>
                           <p className="font-mono">- {book.volumeInfo.description}.
                           </p>
                          </Scrollbars>
                    </Collapse>

<div class="mt-3 mb-[5px]">
  <AwesomeButton onPress={()=>this.passValues([book.volumeInfo.title,book.volumeInfo.language,book.volumeInfo.publishedDate,book.volumeInfo.pageCount])}  type="secondary">Sauvegarder</AwesomeButton>        
</div>        
  <br></br>
      </div>
    </div>
</div>
))}
</div> 
<br></br>
{/* Overflow end! */}
</div>
</div>
);}}