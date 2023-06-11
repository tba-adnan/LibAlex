import React, { Component } from 'react';
import axios from 'axios';
import { AwesomeButton } from 'react-awesome-button';
import cogoToast from "cogo-toast";
import { BeakerIcon, TrashIcon } from "@primer/octicons-react";
import { Configuration, OpenAIApi } from "openai";

const OPENAI_API_KEY = "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";

export default class Saved extends Component {
  constructor() {
    super();
    this.state = {
      savings: [],
      showModal: false,
      selectedBook: null,
      bookSummary: '',
      language: 'en',
      isLoading: false,
      uuid: '',
    };

    this.delBook = this.delBook.bind(this);
  }

  componentDidMount() {
    axios.get('http://127.0.0.1:8000/api/v1/display')
      .then(res => {
        this.setState({ savings: res.data.savings });
      })
      .catch(err => {
        console.log(err);
      });

    const uuid = localStorage.getItem('uuid');
    if (uuid) {
      this.setState({ uuid });
    }
  }

  delBook = (id) => {
    console.log(id);
    axios.get("http://127.0.0.1:8000/api/v1/delete/" + id)
      .then((response) => {
        this.setState({
          savings: this.state.savings.filter(book => book.id !== id)
        });
      });
    cogoToast.success("Sauvegarde du livre supprimé avec succès!", {
      position: 'bottom-right',
      heading: 'Succès :'
    });
  }

  getBookSummary = async (book) => {
    this.setState({
      selectedBook: book,
      showModal: true,
      isLoading: true
    });

    const configuration = new Configuration({
      apiKey: OPENAI_API_KEY
    });
    const openai = new OpenAIApi(configuration);
    const prompt = `give the summary of the book called ${book.book_title} in french in less than 50 word`;

    try {
      const response = await openai.createCompletion({
        model: "text-davinci-003",
        prompt: prompt,
        temperature: 0,
        max_tokens: 100
      });
      const summary = response.data.choices[0].text;
      this.setState({ bookSummary: summary });
    } catch (error) {
      console.log("Error:", error);
    } finally {
      this.setState({ isLoading: false });
    }
  }

  closeModal = () => {
    this.setState({
      selectedBook: null,
      bookSummary: '',
      showModal: false
    });
  }

  switchLanguage = () => {
    const { language } = this.state;
    const newLanguage = language === 'en' ? 'fr' : 'en';
    this.setState({ language: newLanguage });
  }

  copyUUID = () => {
    const textField = document.createElement('textarea');
    textField.innerText = this.state.uuid;
    document.body.appendChild(textField);
    textField.select();
    document.execCommand('copy');
    textField.remove();
    cogoToast.success("Token copied to clipboard!", {
      position: 'bottom-right',
      heading: 'Success : '
    });
  }

  render() {
    const { language, isLoading, uuid } = this.state;

    return (
      <div>
        <div>
          <nav className="font-sans flex flex-col text-center sm:flex-row sm:text-left sm:justify-between py-4 px-6 bg-white shadow-md sm:items-baseline w-full">
            <div className="mb-2 sm:mb-0">
              <p className="text-3xl bold italic pl-6">📚 LibAlex</p>
            </div>
            <div className="space-x-4">
              <AwesomeButton type="secondary" href="/">{language === 'en' ? 'Search more 🔎' : 'Chercher plus 🔎'}</AwesomeButton>
              <AwesomeButton type="primary" onPress={this.copyUUID}>{language === 'en' ? 'Token 🎟️' : 'Jeton 🎟️'}</AwesomeButton>
              <AwesomeButton type="primary" href="/savings">Beta</AwesomeButton>
              <AwesomeButton type="primary" onPress={this.switchLanguage}>{language === 'en' ? 'FR' : 'EN'}</AwesomeButton>
            </div>
          </nav>
        </div>

        <div className=''>
          <div className="flex flex-wrap">
            {this.state.savings.map(book => (
              <div key={book.id} className="w-1/3 p-4">
                <div className="rounded-lg relative">
  <div className="p-6 bg-white rounded-lg shadow-2xl">
    <h3 className="text-lg font-serif mb-4">{book.book_title}:</h3>
    <div className="flex flex-col space-y-2">
      <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-green-600 bg-green-200 last:mr-0 mr-1">Pages: {book.page_count}</span>
      <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-blue-600 bg-blue-200 last:mr-0 mr-1">Language: {book.language}</span>
      <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded text-violet-600 bg-violet-200 last:mr-0 mr-1">Date: {book.release_date}</span>
    </div>

    <div className="mt-6 space-x-2">
      <AwesomeButton type="secondary" onPress={() => this.getBookSummary(book)}>
        {language === 'en' ? 'Book summary in AI 🤖✨' : 'Résumé de livre en IA 🤖✨'}
      </AwesomeButton>
      <AwesomeButton type="danger" onPress={() => this.delBook(book.id)}>
        {language === 'en' ? 'Delete 🗑️' : 'Supprimer 🗑️'}
      </AwesomeButton>
    </div>
  </div>
  <div className="absolute inset-0 rounded-lg ring-4 ring-blue-400 opacity-50"></div>
</div>

              </div>
            ))}
          </div>
        </div>

        {/* Modal */}
        {this.state.showModal && (
          <div className="fixed z-10 inset-0 overflow-y-auto">
            <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
              <div className="fixed inset-0 transition-opacity" aria-hidden="true">
                <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
              </div>

              <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

              <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                  <div className="sm:flex sm:items-start">
                    <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
                      <h3 className="text-lg font-semibold leading-6">{this.state.selectedBook.book_title} :</h3>
                      <br></br>
                      {isLoading ? (
                        <p className="text-sm italic">
                          <span className="animate-spin mr-1">&#8987;</span>
                          Please wait...
                        </p>
                      ) : (
                        <p className="text-sm italic">{this.state.bookSummary}</p>
                      )}
                      {/* <p className="text-sm font-semibold">UUID/Token: {uuid}</p> */}
                    </div>
                  </div>
                </div>

                <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                  <AwesomeButton type="primary" onPress={this.closeModal}>
                    {language === 'en' ? 'Close' : 'Fermer'}
                  </AwesomeButton>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }
}
