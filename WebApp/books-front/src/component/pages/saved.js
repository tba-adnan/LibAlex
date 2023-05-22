import React, { Component } from 'react';
import axios from 'axios';
import { AwesomeButton } from 'react-awesome-button';
import cogoToast from "cogo-toast";
import { BeakerIcon, TrashIcon } from "@primer/octicons-react"; // custom icons

export default class Saved extends Component {
    constructor(){
        super();
        this.state = {savings: []}
// 
        this.delBook = this.delBook.bind(this);
    }

  componentDidMount() {
    axios.get('http://127.0.0.1:8000/api/v1/display').then(res => {
        this.setState({ savings: res.data.savings });})
      .catch(err => {console.log(err);});}

delBook = (id) => {
        console.log(id);
        axios.get("http://127.0.0.1:8000/api/v1/delete/"+id).then((response) => {
        this.setState({ savings: this.state.savings.filter(book => book.id !== id) });
        });
        cogoToast.success("Sauvegarde du livre supprimé avec succès!", { position: 'top-right', heading: 'Succès' });
      }
    
      
  

  render() {
    return (


<div>

<div>
      <nav class="font-sans flex flex-col text-center sm:flex-row sm:text-left sm:justify-between py-4 px-6 bg-white shadow sm:items-baseline w-full">
        <div class="mb-2 sm:mb-0">
        </div>
        <div class="space-x-4">
        <AwesomeButton type="secondary" href="/">cherchez</AwesomeButton>
        <AwesomeButton type="primary" href="/savings">Beta</AwesomeButton>
        </div>
      </nav>
</div>



<div>
      <div className="flex flex-wrap">
        {this.state.savings.map(book => (
          <div key={book.id} className="w-1/3 p-4">
            <div className="bg-white rounded-lg shadow-2xl">
              <div className="p-6">
                <h3 className="text-lg font-serif mb-4">{book.book_title} :</h3>
<div className='flex flex-col space-y-2'>
<span class="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-green-600 bg-green-200 uppercase last:mr-0 mr-1">Pages : {book.page_count}</span>
<span class="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-blue-600 bg-blue-200 uppercase last:mr-0 mr-1">Language : {book.language}</span>
<span class="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-violet-600 bg-violet-200 uppercase last:mr-0 mr-1">Date : {book.release_date}</span>
</div>

<div class="mt-6">
                <AwesomeButton type="danger" onPress={()=>this.delBook(book.id)}>Supprimer</AwesomeButton>
</div>
              </div>
            </div>
          </div>
        ))}
      </div>
      </div>
      </div>
    );
  }
}
