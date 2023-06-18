import React, { Component } from 'react';
import axios from 'axios';
import { AwesomeButton } from 'react-awesome-button';
import cogoToast from "cogo-toast";
import { BeakerIcon, TrashIcon } from "@primer/octicons-react";
import { Configuration, OpenAIApi } from "openai";
import QRCode from 'qrcode.react';

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
      showTokenModal: false,
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

  showTokenModal = () => {
    this.setState({ showTokenModal: true });
  };

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
              <AwesomeButton type="primary" onPress={this.showTokenModal}>{language === 'en' ? 'Token 🎟️' : 'Jeton 🎟️'}</AwesomeButton>
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

        {/* Token Modal */}
        {this.state.showTokenModal && (
          <div className="fixed z-10 inset-0 overflow-y-auto">
            <div className="flex items-center justify-center min-h-screen">
              <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
              <div className="bg-white rounded-lg p-8 shadow-2xl relative">
                <h2 className="text-xl font-bold mb-4">{language === 'en' ? '🎟️ Token : ' : ' 🎟️ Token : '}</h2>
                <QRCode value={this.state.uuid} />
                <p className="text-sm mt-4 mb-2">{language === 'en' ? 'Your Token : ' : 'Votre jeton : '}</p>
                <div className="flex space-x-2">
                  <input
                    type="text"
                    value={uuid}
                    disabled
                    className="bg-gray-200 px-2 py-1 rounded"
                  />
                  <AwesomeButton type="primary" onPress={this.copyUUID}>
                    {language === 'en' ? 'Copy' : 'Copier'}
                  </AwesomeButton>
                </div>
                <p className="text-sm mt-2 pb-1">{language === 'en' ? 'Scan the QR code to access your books from the mobile app.' : 'Scannez le code QR pour accéder à vos livres à partir de l\'application mobile'}</p>
                <AwesomeButton
                  type="secondary"
                  onPress={() => this.setState({ showTokenModal: false })}
                >
                  {language === 'en' ? 'Close' : 'Fermer'}
                </AwesomeButton>
              </div>
            </div>
          </div>
        )}

        {/* Summary Modal */}
        {this.state.showModal && (
  <div className="fixed z-10 inset-0 overflow-y-auto">
    <div className="flex items-center justify-center min-h-screen">
      <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
      <div className="bg-white rounded-lg p-8 shadow-xl relative">
        <h2 className="text-xl font-bold mb-4">{language === 'en' ? '🤖 AI Book Summary : ' : '🤖 Résumé de Livre en IA : '}</h2>
        {isLoading ? (
          <div className="flex items-center justify-center shadow-2xl">
            <BeakerIcon size={32} className="animate-spin mr-2" />
            <p>{language === 'en' ? ' Generating summary...' : 'Génération du résumé...'}</p>
          </div>
        ) : (
          <div className="max-w-md">
            <p className="mb-4">{this.state.bookSummary}</p>
            <AwesomeButton type="primary" onPress={this.closeModal}>
              {language === 'en' ? 'Close' : 'Fermer'}
            </AwesomeButton>
          </div>
        )}
      </div>
    </div>
  </div>
)}

      </div>
    );
  }
}
